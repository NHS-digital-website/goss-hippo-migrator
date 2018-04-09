<#-- @ftlvariable name="general" type="uk.nhs.digital.gossmigrator.model.hippo.General" -->
{
"name" : "${general.jcrNodeName}",
"primaryType" : "website:general",
"mixinTypes" : [ "mix:versionable", "mix:referenceable" ],
"properties" : [ {
"name" : "website:gossid",
"type" : "LONG",
"multiple" : false,
"values" : [ "${general.stringId}" ]
},{
"name" : "website:seosummary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${general.seoSummary}" ]
}, {
"name" : "hippotranslation:locale",
"type" : "STRING",
"multiple" : false,
"values" : [ "en" ]
}, {
"name" : "website:title",
"type" : "STRING",
"multiple" : false,
"values" : [ "${general.title}" ]
}, {
"name" : "common:searchable",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "true" ]
}, {
"name" : "website:shortsummary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${general.shortSummary}" ]
}, {
"name" : "website:summary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${general.summary}" ]
}, {
"name" : "jcr:path",
"type" : "STRING",
"multiple" : false,
"values" : [ "${general.jcrPath}" ]
}, {
"name" : "jcr:localizedName",
"type" : "STRING",
"multiple" : false,
"values" : [ "${general.localizedName}" ]
},{
"name" : "website:type",
"type" : "STRING",
"multiple" : false,
"values" : [ "${general.type}" ]
} ],
"nodes" : [ <#assign firstNode = true>
<#if general.sections??> <#if firstNode==false>,<#else><#assign firstNode=false></#if><#list general.sections as section>{
"name" : "website:sections",
"primaryType" : "website:section",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "website:title",
"type" : "STRING",
"multiple" : false,
"values" : [ "${section.title}" ]
}, {
"name" : "website:type",
"type" : "STRING",
"multiple" : false,
"values" : [ "${section.type}" ]
} ],
"nodes" : [
{
"name" : "website:html",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${section.content.content}" ]
} ],
"nodes" : [ <#list section.content.docReferences as refs> {
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
}<#sep>, </#sep> </#list>]}
]}
<#-- End list general.sections --><#sep>, </#sep></#list>
<#-- End if general.sections?? --></#if>
<#if general.component??><#if firstNode==false>,<#else><#assign firstNode=false></#if>
{
"name" : "website:component",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${general.component.content}" ]
} ],
"nodes" : [ <#list general.component.docReferences as refs> {
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
}<#sep>, </#sep> </#list> ]
}</#if>
]}