<?xml version="1.0" encoding="UTF-8"?>
<!-- ============================================================
     Docbook customization

     This file solves the issue of docbook using the <h2> elements
     both for chapter and for the first level of sections heading.
     ============================================================= -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- Symbolic URN specific to docbkx-maven-plugin -->
  <xsl:import href="urn:docbkx:stylesheet/docbook.xsl"/>

  <!--
       Following is copied from "http://docbook.sourceforge.net/release/xsl/current/xhtml/component.xsl".
       Only the lines identified by a comment have been modified.  This file is used only for building
       the web site and is not included in the Geotk distributions.
  -->

  <xsl:template name="component.title">
    <xsl:param name="node" select="."/>
    <xsl:variable name="level">
      <xsl:choose>
        <xsl:when test="ancestor::section">
          <xsl:value-of select="count(ancestor::section)+1"/>
        </xsl:when>
        <xsl:when test="ancestor::sect5">6</xsl:when>
        <xsl:when test="ancestor::sect4">5</xsl:when>
        <xsl:when test="ancestor::sect3">4</xsl:when>
        <xsl:when test="ancestor::sect2">3</xsl:when>
        <xsl:when test="ancestor::sect1">2</xsl:when>
        <xsl:when test="ancestor::sect0">1</xsl:when>   <!-- Added -->
        <xsl:otherwise>0</xsl:otherwise>  <!-- Was 1, changed to 0 -->
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="h{$level+1}" namespace="http://www.w3.org/1999/xhtml">
      <xsl:attribute name="class">title</xsl:attribute>
      <xsl:if test="$generate.id.attributes = 0">
        <xsl:call-template name="anchor">
	  <xsl:with-param name="node" select="$node"/>
          <xsl:with-param name="conditional" select="0"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:apply-templates select="$node" mode="object.title.markup">
        <xsl:with-param name="allow-anchors" select="1"/>
      </xsl:apply-templates>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
