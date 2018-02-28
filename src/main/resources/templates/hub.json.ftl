<#-- @ftlvariable name="hub" type="uk.nhs.digital.gossmigrator.model.hippo.Hub" -->
{
"name" : "${hub.jcrNodeName}",
"primaryType" : "website:hub",
"mixinTypes" : [ "mix:versionable", "mix:referenceable" ],
"properties" : [ {
"name" : "hippotranslation:locale",
"type" : "STRING",
"multiple" : false,
"values" : [ "en" ]
}, {
"name" : "website:seosummary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.seoSummary}" ]
}, {
"name" : "website:title",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.title}" ]
}, {
"name" : "website:summary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.summary}" ]
}, {
"name" : "common:searchable",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "true" ]
}, {
"name" : "website:shortsummary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.shortSummary}" ]
}, {
"name" : "jcr:path",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.jcrPath}" ]
}, {
"name" : "jcr:localizedName",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.localizedName}" ]
}, {
"name" : "website:listtitle",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.listTitle}" ]
} ],
"nodes":[ <#assign firstNode = true>
<#if hub.body?has_content> <#if firstNode==false>,<#else><#assign firstNode=false></#if>
{
"name" : "website:body",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.body.content}" ]
} ],
"nodes" : [ ]
}
</#if>
<#if hub.component??><#if firstNode==false>,<#else><#assign firstNode=false></#if>
{
"name" : "website:component",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.component.content}" ]
} ],
"nodes" : [ ]
}</#if>
<#if hub.componentPaths?has_content> <#if firstNode==false>,<#else><#assign firstNode=false></#if>
<#list hub.componentPaths as component>{
"name" : "website:componentlist",
"primaryType" : "hippo:mirror",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippo:docbase",
"type" : "STRING",
"multiple" : false,
"values" : [ "${component}" ]
} ],
"nodes" : [ ]
}<#sep>, </#sep></#list>
</#if>
]}