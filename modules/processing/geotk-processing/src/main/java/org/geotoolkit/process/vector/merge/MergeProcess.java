/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.vector.merge;


import com.vividsolutions.jts.geom.Geometry;

import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.mapping.MappingUtils;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.vector.merge.MergeDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Merge many FeatureCollection in one. The fist FeatureCollection found in the input Collection
 * have his FeatureType preserved. The others will be adapted to this one.
 * @author Quentin Boileau
 * @module pending
 */
public class MergeProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public MergeProcess(final ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() {
        final FeatureCollection[] inputFeaturesList = value(FEATURES_IN, inputParameters);
        final FeatureCollection firstFC = inputFeaturesList[0];

        final FeatureCollection resultFeatureList = new MergeFeatureCollection(inputFeaturesList,firstFC);

        getOrCreate(FEATURE_OUT, outputParameters).setValue(resultFeatureList);
    }

    /**
    * Create a new feature based on common attributes form the base FeatureType.
    * @param feature
    * @param newFeatureType
    * @param conversionMap
    * @return a feature
    * @throws NonconvertibleObjectException
    */
    static Feature mergeFeature(final Feature feature,final FeatureType newFeatureType, final Map<Name, ObjectConverter> conversionMap)
            throws NonconvertibleObjectException {

        if(conversionMap == null) {
            return feature;
        }

        final Feature mergedFeature = FeatureUtilities.defaultFeature(newFeatureType, feature.getIdentifier().getID());

        for (final Map.Entry<Name,ObjectConverter> entry : conversionMap.entrySet()) {
            if(entry.getValue() == null) {
                mergedFeature.getProperty(entry.getKey()).setValue(feature.getProperty(entry.getKey()).getValue());
            }else{
                mergedFeature.getProperty(entry.getKey()).setValue(entry.getValue().convert(feature.getProperty(entry.getKey()).getValue()));
            }

        }
        return mergedFeature;
    }


    /**
    * Create a map between two FeatureType. Each entry of the map represents a shared attribute between
    * two input FeatureType. The key contained the name of attribute and the value a ObjectConverter if the
    * type between attributes is different.
    * @param input
    * @param toConvert
    * @return map<Name, ObjectConverter>. Return null if input FeatureType are equals
    * @throws NonconvertibleObjectException
    */
    static Map<Name, ObjectConverter> createConversionMap (final FeatureType input, final FeatureType toConvert) throws NonconvertibleObjectException{

        if(input.equals(toConvert)) {
            return null;
        }
        final Map<Name, ObjectConverter> map = new HashMap<Name, ObjectConverter>();

        for (final PropertyDescriptor toConvertDesc : toConvert.getDescriptors()) {
            for(final PropertyDescriptor inputDesc : input.getDescriptors()) {

                //same property name
                if(toConvertDesc.getName().equals(inputDesc.getName())) {

                    final Class inputClass = inputDesc.getType().getBinding();
                    final Class toConvertClass = toConvertDesc.getType().getBinding();
                    if(toConvertClass.equals(inputClass)) {
                        //same name and same type
                        map.put(toConvertDesc.getName(), null);
                    }else{
                        //same name but different type
                        if(toConvertDesc instanceof GeometryDescriptor) {
                           map.put(toConvertDesc.getName(), new GeomConverter(toConvertClass, inputClass));
                        }else{
                            map.put(toConvertDesc.getName(), ConverterRegistry.system().converter(toConvertClass, inputClass));
                        }
                    }
                }
            }
        }
        return map;

    }

    /**
    * Implementation of ObjectConverter for JTS Geometry using the MappingUtils class.
    * This class is use to Convert from a JTS Geometry to an other giving an ObjectConverter object.
    * @author Quentin Boileau
    * @module pending
    */
    private static class GeomConverter implements ObjectConverter<Object, Object> {

        private final Class sourceClass;
        private final Class targetClass;

        /**
         * GeomConverter constructor
         * @param source
         * @param target
         */
        public GeomConverter(final Class source, final Class target) {
            sourceClass = source;
            targetClass = target;
        }

        @Override
        public Class<? super Object> getSourceClass() {
            return sourceClass;
        }

        @Override
        public Class<? extends Object> getTargetClass() {
            return targetClass;
        }

        @Override
        public boolean hasRestrictions() {
           return true;
        }

        @Override
        public boolean isOrderPreserving() {
            return true;
        }

        @Override
        public boolean isOrderReversing() {
            return false;
        }

        @Override
        public Object convert(final Object s) throws NonconvertibleObjectException {
            return MappingUtils.convertType((Geometry)s, getTargetClass());
        }
    }

}