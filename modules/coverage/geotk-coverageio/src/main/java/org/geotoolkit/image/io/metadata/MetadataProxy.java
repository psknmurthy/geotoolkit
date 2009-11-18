/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.image.io.metadata;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;

import org.opengis.util.CodeList;
import org.opengis.metadata.content.Band;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.metadata.KeyNamePolicy;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.resources.Errors;


/**
 * Implementation of metadata interfaces. Calls to getter methods are converted into calls to the
 * metadata accessor extracting an attribute from the {@link javax.imageio.metadata.IIOMetadata}
 * object.
 *
 * @param <T> The metadata interface implemented by the proxy.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 * @module
 */
final class MetadataProxy<T> implements InvocationHandler {
    /**
     * {@code true} for enabling the process of a few Geotk-specific special cases. This field
     * should always be {@code true}. It is defined mostly as a way to spot every places where
     * some special cases are defined.
     */
    private static final boolean SPECIAL_CASE = true;

    /**
     * The interface implemented by the proxy.
     */
    final Class<T> interfaceType;

    /**
     * The metadata accessor. This is used for fetching the value of an attribute. The name of
     * the attribute is inferred from the method name using the {@linkplain #namesMapping} map.
     */
    final MetadataAccessor accessor;

    /**
     * The index of the child element, or -1 if none.
     */
    private final int index;

    /**
     * The mapping from method names to attribute names, or {@code null} if this mapping
     * is unknown. Keys are method names, and values are the attribute name as determined
     * by {@link SpatialMetadataFormat#NAME_POLICY}.
     */
    private final Map<String, String> namesMapping;

    /**
     * The childs created up to date. This is used only when the return type of some
     * invoked methods is a {@link java.util.Collection}, {@link java.util.List} or
     * an other metadata interface.
     * <p>
     * The keys are method names (instead than attribute names) because they are
     * usually internalized by the JVM, which is not the case of the attribute names.
     */
    private transient Map<String, Object> childs;

    /**
     * Creates a new proxy having the same properties than the given one except for the index.
     * This is used for creating elements in a list.
     */
    private MetadataProxy(final MetadataProxy<T> prototype, final int index) {
        interfaceType = prototype.interfaceType;
        namesMapping  = prototype.namesMapping;
        accessor      = prototype.accessor;
        this.index    = index;
    }

    /**
     * Creates a new proxy for the given metadata accessor.
     */
    MetadataProxy(final Class<T> type, final MetadataAccessor accessor) {
        interfaceType = type;
        this.accessor = accessor;
        this.index    = -1;
        final IIOMetadataFormat format = accessor.format;
        if (format instanceof SpatialMetadataFormat) {
            final MetadataStandard standard = ((SpatialMetadataFormat) format).getElementStandard(accessor.name());
            if (standard != null) {
                Class<?> t = type;
                if (SPECIAL_CASE) {
                    /*
                     * If the metadata standard is ISO 19115, then we must process SampleDimension
                     * especially because this interface is not defined by ISO 19115. It is a Geotk
                     * interface designed as a sub-set of the ISO Band interface, plus a few additions.
                     */
                    if (MetadataStandard.ISO_19115.equals(standard)) {
                        if (SampleDimension.class.equals(type)) {
                            t = Band.class;
                        }
                    }
                }
                namesMapping = standard.asNameMap(t, SpatialMetadataFormat.NAME_POLICY, KeyNamePolicy.METHOD_NAME);
                return;
            }
        }
        namesMapping = null;
    }

    /**
     * Returns a new instance of a proxy class for the specified metadata interface.
     *
     * @param  type     The interface for which to create a proxy instance.
     * @param  accessor The metadata accessor.
     * @throws IllegalArgumentException If the given type is not a valid interface
     *         (see {@link Proxy} javadoc for a list of the conditions).
     */
    static <T> T newProxyInstance(final Class<T> type, final MetadataAccessor accessor) {
        return type.cast(Proxy.newProxyInstance(MetadataProxy.class.getClassLoader(),
                new Class<?>[] {type}, new MetadataProxy<T>(type, accessor)));
    }

    /**
     * Returns a new instance of a proxy class with the same properties than this instance
     * but a different index. This is used for creating elements in a list.
     */
    final T newProxyInstance(final int index) {
        final Class<T> type = interfaceType;
        return type.cast(Proxy.newProxyInstance(MetadataProxy.class.getClassLoader(),
                new Class<?>[] {type}, new MetadataProxy<T>(this, index)));
    }

    /**
     * Returns the attribute name for the given method name. The caller must have verified
     * that the method name starts with either {@code "get"}, {@code "set"} or {@code "is"}.
     *
     * @param  methodName The value of {@link Method#getName()}.
     * @return The name of the attribute to search in the {@code IIOMetadataNode}
     *         wrapped by the {@linkplain #accessor}.
     */
    @SuppressWarnings("fallthrough")
    private String getAttributeName(final String methodName) {
        if (namesMapping != null) {
            final String attribute = namesMapping.get(methodName);
            if (attribute != null) {
                return attribute;
            }
        }
        /*
         * If no mapping is explicitly declared for the given method name, apply JavaBeans
         * conventions. If the prefix is not "is", the code below assumes "get" or "set".
         */
        final int offset = methodName.startsWith("is") ? 2 : 3; // Prefix length
        switch (methodName.length() - offset) {
            default: {
                /*
                 * If there is at least 2 characters after the prefix, assume that
                 * we have an acronym if the two first character are upper case.
                 */
                if (Character.isUpperCase(methodName.charAt(offset)) &&
                    Character.isUpperCase(methodName.charAt(offset+1)))
                {
                    return methodName.substring(offset);
                }
                // Fall through
            }
            case 1: {
                /*
                 * If we have at least one character, make the first character lower-case.
                 */
                return Character.toLowerCase(methodName.charAt(offset)) + methodName.substring(offset + 1);
            }
            case 0: {
                /*
                 * If we have only the prefix, return it unchanged.
                 */
                return methodName;
            }
        }
    }

    /**
     * Returns the type of user object for the given element. This typically equals to the
     * {@linkplain Method#getReturnType() method return type}, but is some occasion the
     * {@link IIOMetadataFormat} forces a sub-type.
     *
     * @param  name The element name.
     * @param  methodType The type inferred from the method signature, or {@code null} if unknown.
     * @return The type to use, which is garanteed to be assignable to the method type.
     * @throws IllegalArgumentException If the named element does not exist or does not define objects.
     */
    private Class<?> getElementClass(final String name, final Class<?> methodType) throws IllegalArgumentException {
        final Class<?> declaredType = accessor.format.getObjectClass(name); // Not allowed to be null.
        return (methodType == null || methodType.isAssignableFrom(declaredType)) ? declaredType : methodType;
    }

    /**
     * Invoked when a method from the metadata interface has been invoked.
     *
     * @param  proxy  The proxy instance that the method was invoked on.
     * @param  method The method from the interface which have been invoked.
     * @param  args   The arguments, or {@code null} if the method takes no argument.
     * @return The value to return from the method invocation on the proxy instance.
     * @throws UnsupportedOperationException If the given method is not supported.
     * @throws IllegalArgumentException If {@code args} contains a value while none was expected.
     * @throws IllegalStateException If the attribute value can not be converted to the return type.
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args)
            throws UnsupportedOperationException, IllegalArgumentException, IllegalStateException
    {
        /*
         * We accept only calls to getter methods, except for a few non-final
         * methods defined in Object that we need to define for compliance.
         */
        final String methodName = method.getName();
        final int numArgs = (args != null) ? args.length : 0;
        if (!methodName.startsWith("get") && !methodName.startsWith("is")) {
            switch (numArgs) {
                case 0: {
                    if (methodName.equals("toString")) {
                        return accessor.toString(interfaceType);
                    }
                    if (methodName.equals("hashCode")) {
                        return System.identityHashCode(proxy);
                    }
                    break;
                }
                case 1: {
                    if (methodName.equals("equals")) {
                        return proxy == args[0];
                    }
                    break;
                }
            }
            throw new UnsupportedOperationException(Errors.format(
                    Errors.Keys.UNKNOW_COMMAND_$1, methodName));
        }
        if (numArgs != 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_$1, methodName));
        }
        /*
         * Gets the name of the attribute to fetch, and set the accessor
         * child index on the children represented by this proxy (if any).
         */
        final MetadataAccessor accessor = this.accessor;
        final String name = getAttributeName(methodName);
        if (index >= 0) {
            accessor.selectChild(index);
        } else {
            accessor.selectParent();
        }
        /*
         * First, process the cases that are handled in a special way. The order is significant:
         * if the target type is some generic type like java.lang.Object, then we want to select
         * the method performing the less transformation (String if the target type is Object,
         * Double rather than Integer if the target type is Number).
         */
        final Class<?> targetType = Classes.primitiveToWrapper(method.getReturnType());
        if (targetType.isAssignableFrom(String     .class)) return accessor.getAttributeAsString  (name);
        if (targetType.isAssignableFrom(Double     .class)) return accessor.getAttributeAsDouble  (name);
        if (targetType.isAssignableFrom(Float      .class)) return accessor.getAttributeAsFloat   (name);
        if (targetType.isAssignableFrom(Integer    .class)) return accessor.getAttributeAsInteger (name);
        if (targetType.isAssignableFrom(Boolean    .class)) return accessor.getAttributeAsBoolean (name);
        if (targetType.isAssignableFrom(double[]   .class)) return accessor.getAttributeAsDoubles (name, false);
        if (targetType.isAssignableFrom(int[]      .class)) return accessor.getAttributeAsIntegers(name, false);
        if (targetType.isAssignableFrom(Date       .class)) return accessor.getAttributeAsDate    (name);
        if (targetType.isAssignableFrom(NumberRange.class)) return accessor.getAttributeAsRange   (name);
        if (targetType.isAssignableFrom(Citation   .class)) return accessor.getAttributeAsCitation(name);
        if (targetType.isAssignableFrom(List.class)) {
            /*
             * TODO: process after the line below the cases that are not collection of
             *       metadata. For example it could be a collection of dates.
             */
            Class<?> componentType = Classes.boundOfParameterizedAttribute(method);
            /*
             * For lists, we instantiate MetadataProxyList only when first needed and cache
             * the result for reuse. The type of elements are garanteed to be compatible
             * with the type declared in the method signature (inferred from generic types).
             * However it can also be restricted to a subtype, if the metadata format makes
             * such restriction.
             */
            if (childs == null) {
                childs = new HashMap<String, Object>();
            }
            List<?> list = (List<?>) childs.get(methodName);
            if (list == null) {
                final String elementName = SpatialMetadataFormat.toElementName(name);
                final MetadataAccessor acc = new MetadataAccessor(accessor, elementName, "#auto");
                if (acc.allowsChildren()) {
                    componentType = getElementClass(acc.childPath, componentType);
                    list = MetadataProxyList.create(componentType, acc);
                } else {
                    componentType = getElementClass(elementName, componentType);
                    list = Collections.singletonList(newProxyInstance(componentType, acc));
                }
                childs.put(methodName, list);
            }
            return list;
        }
        /*
         * Code lists case. Only existing instances are returned; no new instance is created.
         */
        if (CodeList.class.isAssignableFrom(targetType)) {
            @SuppressWarnings({"unchecked","rawtypes"})
            final CodeList code = accessor.getAttributeAsCode(name, (Class) targetType);
            return code;
        }
        /*
         * For all other types, assume a nested child element.
         * A new proxy will be created for the nested child.
         */
        if (childs == null) {
            childs = new HashMap<String, Object>();
        }
        Object child = childs.get(methodName);
        if (child == null) {
            try {
                // Each of the next 4 lines may throw, directly or indirectly, an IllegalArgumentException.
                final String   elementName = SpatialMetadataFormat.toElementName(name);
                final Class<?> elementType = getElementClass(elementName, targetType);
                final MetadataAccessor acc = new MetadataAccessor(accessor, elementName, "#auto");
                child = newProxyInstance(elementType, acc);
            } catch (IllegalArgumentException e) {
                // Report the warning and remember that we can not return a
                // value for this element, so we don't try again next time.
                accessor.warning(interfaceType, methodName, e);
                child = Void.TYPE;
            }
            childs.put(methodName, child);
        }
        return (child != Void.TYPE) ? child : null;
    }

    /**
     * Returns a string representation of the {@linkplain #accessor}, but declaring the
     * class as {@code MetadataProxy} instead than {@code MetadataAccessor}.
     */
    @Override
    public String toString() {
        return accessor.toString(getClass());
    }
}