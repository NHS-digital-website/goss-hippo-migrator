<#-- @ftlvariable name="listpage" type="uk.nhs.digital.gossmigrator.model.hippo.ListPage" -->
{
  "name" : "component-list-document",
  "primaryType" : "website:componentlist",
  "mixinTypes" : [ "mix:referenceable" ],
  "properties" : [ {
    "name" : "website:seosummary",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${listpage.seoSummary}" ]
  }, {
    "name" : "website:title",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${listpage.title}" ]
  }, {
    "name" : "hippotranslation:locale",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "en" ]
  }, {
    "name" : "website:anchor",
    "type" : "BOOLEAN",
    "multiple" : false,
    "values" : [ "false" ]
  }, {
    "name" : "hippo:availability",
    "type" : "STRING",
    "multiple" : true,
    "values" : [ "live" ]
  }, {
    "name" : "common:searchable",
    "type" : "BOOLEAN",
    "multiple" : false,
    "values" : [ "true" ]
  }, {
    "name" : "website:shortsummary",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${listpage.shortSummary}" ]
  }, {
    "name" : "website:summary",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${listpage.summary}" ]
  }, {
    "name" : "jcr:path",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${listpage.jcrPath}" ]
  }, {
    "name" : "jcr:localizedName",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${listpage.localizedName}" ]
  } ],
  "nodes" : [ {
    "name" : "website:body",
    "primaryType" : "hippostd:html",
    "mixinTypes" : [ ],
    "properties" : [ {
      "name" : "hippostd:content",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${listpage.body.content}" ]
    } ],
    "nodes" : [ <#list listpage.body.docReferences as refs> {
      "name" : "${refs.nodeName}",
      "primaryType" : "hippo:facetselect",
      "mixinTypes" : [ ],
      "properties" : [ {
      "name" : "hippo:facets",
      "type" : "STRING",
      "multiple" : true,
      "values" : [ ]
    }, {
      "name" : "hippo:values",
      "type" : "STRING",
      "multiple" : true,
      "values" : [ ]
    }, {
      "name" : "hippo:docbase",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${refs.jcrPath}" ]
    }, {
      "name" : "hippo:modes",
      "type" : "STRING",
      "multiple" : true,
      "values" : [ ]
      } ],
      "nodes" : [ ]
    }<#sep>, </#sep> </#list>]<#if listpage.externalLinks??>, <#list listpage.externalLinks as externalLink>{
    "name" : "website:items",
    "primaryType" : "website:externallink",
    "mixinTypes" : [ ],
    "properties" : [ {
      "name" : "website:link",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${externalLink.address}" ]
    }, {
      "name" : "website:shortsummary",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${externalLink.description}" ]
    }, {
      "name" : "website:title",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${externalLink.displayText}" ]
    } ],
    "nodes" : [ ]
  }<#sep>, </#sep></#list></#if><#if listpage.internalLinks??>, <#list listpage.internalLinks as internalLink>{
    "name" : "website:items",
    "primaryType" : "website:internallink",
    "mixinTypes" : [ ],
    "properties" : [ ],
    "nodes" : [ {
      "name" : "website:link",
      "primaryType" : "hippo:mirror",
      "mixinTypes" : [ ],
      "properties" : [ {
        "name" : "hippo:docbase",
        "type" : "STRING",
        "multiple" : false,
        "values" : [ "${internalLink}" ]
      } ],
      "nodes" : [ ]
    } ]
  }<#sep>, </#sep></#list></#if> ]
}