<#-- @ftlvariable name="hub" type="uk.nhs.digital.gossmigrator.model.hippo.Hub" -->
{
"name" : "${hub.jcrNodeName}",
"primaryType" : "website:hub",
"mixinTypes" : [ "mix:referenceable" ],
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
"name" : "jcr:localizedName",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.localizedName}" ]
}, {
"name" : "website:listTitle",
"type" : "STRING",
"multiple" : false,
"values" : [ "${hub.listTitle}" ]
} ],
"nodes":[ <#assign firstNode = true>
<#if hub.sections?has_content> <#if firstNode==false>,<#else><#assign firstNode=false></#if>
{
<#list hub.sections as section>{
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
"nodes" : [ {
"name" : "website:html",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${section.content.content}" ]
} ]
}]}
<#-- End list hub.sections --><#sep>, </#sep></#list>
}</#if>
<#if service.component??><#if firstNode==false>,<#else><#assign firstNode=false></#if>
{
"name" : "website:component",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.component.content}" ]
} ],
"nodes" : [ ]
}</#if>
]