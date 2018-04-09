<#-- @ftlvariable name="service" type="uk.nhs.digital.gossmigrator.model.hippo.Service" -->
{
"name" : "${service.jcrNodeName}",
"primaryType" : "website:service",
"mixinTypes" : [ "mix:referenceable" ],
"properties" : [ {
"name" : "website:gossid",
"type" : "LONG",
"multiple" : false,
"values" : [ "${service.stringId}" ]
},{
"name" : "hippotranslation:locale",
"type" : "STRING",
"multiple" : false,
"values" : [ "en" ]
}, {
"name" : "website:seosummary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.seoSummary}" ]
}, {
"name" : "website:title",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.title}" ]
}, {
"name" : "website:summary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.summary}" ]
}, {
"name" : "common:searchable",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "true" ]
}, {
"name" : "website:shortsummary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.shortSummary}" ]
}, {
"name" : "jcr:path",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.jcrPath}" ]
}, {
"name" : "jcr:localizedName",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.localizedName}" ]
} ],<#--
The nodes array will hold rich text components and complex components.
If will contain one node for each top task, one or no node for introduction
and a node per each section.  Nodes separated by commas that are suppressed
if the preceding nodes did not exist.  This relies on the preceding nodes
bean objects being null.
-->
"nodes" : [ <#assign firstNode = true>
<#if service.topTasks??><#if firstNode==false>,<#else><#assign firstNode=false></#if>
<#list service.topTasks as tasks>{
"name" : "website:toptasks",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${tasks.content}" ]
} ],
"nodes" : [ <#list tasks.docReferences as refs> {
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
} <#sep>, </#sep> </#list>]
}<#sep>, </#sep></#list></#if>
<#if service.introduction??> <#if firstNode==false>,<#else><#assign firstNode=false></#if>{
"name" : "website:introduction",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.introduction.content}" ]
} ],
"nodes" : [ <#list service.introduction.docReferences as refs> {
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
}<#sep>, </#sep> </#list>]
}<#-- End if service.introduction --></#if><#if service.sections??> <#if firstNode==false>,<#else><#assign firstNode=false></#if><#list service.sections as section>{
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
}<#sep>, </#sep> </#list>]
} ]<#-- Closing array section rich text content -->
}<#-- End list service.sections --><#sep>, </#sep></#list><#-- End if service.sections?? --></#if><#if service.contactDetails??> <#if firstNode==false>,<#else><#assign firstNode=false></#if>{
"name" : "website:contactdetails",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${service.contactDetails.content}" ]
} ],
"nodes" : [ <#list service.contactDetails.docReferences as refs> {
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
}<#sep>, </#sep> </#list>]
}<#-- End if service.contactDetails --></#if>
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
"nodes" : [ <#list service.component.docReferences as refs> {
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
<#if service.externalLinks??><#if firstNode==false>,<#else><#assign firstNode=false></#if><#list service.externalLinks as externalLink>{
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
}<#sep>, </#sep></#list></#if>
<#if service.internalLinks??><#if firstNode==false>,<#else><#assign firstNode=false></#if><#list service.internalLinks as internalLink>{
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
}<#sep>, </#sep></#list></#if>
]<#-- Closing array complex nodes -->
}