<#-- @ftlvariable name="publication" type="uk.nhs.digital.gossmigrator.model.hippo.Publication" -->
{
"name" : "${publication.jcrNodeName}",
"primaryType" : "publicationsystem:legacypublication",
"mixinTypes" : [ "mix:versionable", "mix:referenceable", "hippotaxonomy:classifiable" ],
"properties" : [ {
"name" : "common:FacetType",
"type" : "STRING",
"multiple" : false,
"values" : [ "publication" ]
}, {
"name" : "jcr:path",
"type" : "STRING",
"multiple" : false,
"values" : [ "${publication.jcrPath}" ]
}, {
"name" : "jcr:localizedName",
"type" : "STRING",
"multiple" : false,
"values" : [ "content" ]
}, {
"name" : "publicationsystem:Title",
"type" : "STRING",
"multiple" : false,
"values" : [ "${publication.title}" ]
}, {
"name" : "publicationsystem:AdministrativeSources",
"type" : "STRING",
"multiple" : false,
"values" : [ "" ]
},{
"name" : "publicationsystem:InformationType",
"type" : "STRING",
"multiple" : true,
"values" : [ ${publication.informationType} ]
}, {
"name" : "publicationsystem:Granularity",
"type" : "STRING",
"multiple" : true,
"values" : [ ${publication.granuality} ]
}, {
"name" : "publicationsystem:GeographicCoverage",
"type" : "STRING",
"multiple" : false,
"values" : [ ${publication.geographicCoverage} ]
}, {
"name" : "publicationsystem:PubliclyAccessible",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "true" ]
},{
"name" : "hippotranslation:locale",
"type" : "STRING",
"multiple" : false,
"values" : [ "en" ]
}
<#if publication.coverageEnd?has_content>, {
"name" : "publicationsystem:CoverageEnd",
"type" : "DATE",
"multiple" : false,
"values" : [ "${publication.coverageEnd}" ]
}</#if>, {
"name" : "publicationsystem:NominalDate",
"type" : "DATE",
"multiple" : false,
"values" : [ "${publication.publicationDate}" ]
}<#if publication.coverageStart?has_content>, {
"name" : "publicationsystem:CoverageStart",
"type" : "DATE",
"multiple" : false,
"values" : [ "${publication.coverageStart}" ]
}</#if><#if publication.taxonomyKeys?has_content>, {
"name" : "hippotaxonomy:keys",
"type" : "STRING",
"multiple" : true,
"values" : [ <#list publication.taxonomyKeys as key>"${key}"<#sep>, </#sep></#list> ]
}, {
"name" : "common:FullTaxonomy",
"type" : "STRING",
"multiple" : true,
"values" : [ <#list publication.fullTaxonomy as key>"${key}"<#sep>, </#sep></#list> ]
}</#if>
],
"nodes" : [
<#assign firstNode = true>
<#if publication.summary??>
<#if firstNode==false> , <#else><#assign firstNode=false></#if>
{
"name" : "publicationsystem:Summary",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${publication.summary}" ]
}]}
</#if><#if publication.keyFacts??>
<#if firstNode == false> , <#else><#assign firstNode=false></#if>
{
"name" : "publicationsystem:KeyFacts",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${publication.keyFacts.content}" ]
} ]
}
</#if><#if publication.relatedLinks?has_content>
<#if firstNode == false> , <#else><#assign firstNode=false></#if>
<#list publication.relatedLinks as relatedLink>
{
"name" : "publicationsystem:RelatedLinks",
"primaryType" : "publicationsystem:relatedlink",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "publicationsystem:linkText",
"type" : "STRING",
"multiple" : false,
"values" : [ "${relatedLink.displayText}" ]
}, {
"name" : "publicationsystem:linkUrl",
"type" : "STRING",
"multiple" : false,
"values" : [ "${relatedLink.address}" ]
} ],
"nodes" : [ ]
}<#sep>, </#sep></#list></#if>
<#if publication.resourceLinks?has_content>
<#if firstNode == false> , <#else><#assign firstNode=false></#if>
<#list publication.resourceLinks as resourceLink>
{
"name" : "publicationsystem:ResourceLinks",
"primaryType" : "publicationsystem:relatedlink",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "publicationsystem:linkText",
"type" : "STRING",
"multiple" : false,
"values" : [ "${resourceLink.displayText}" ]
}, {
"name" : "publicationsystem:linkUrl",
"type" : "STRING",
"multiple" : false,
"values" : [ "${resourceLink.address}" ]
} ],
"nodes" : [ ]
}<#sep>, </#sep></#list></#if>
<#if publication.files?has_content>
<#if firstNode == false> , <#else><#assign firstNode=false></#if>
<#list publication.files as file>
{
"name" : "publicationsystem:Attachments-v3",
"primaryType" : "publicationsystem:attachment",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "publicationsystem:displayName",
"type" : "STRING",
"multiple" : false,
"values" : [ "${file.displayName}" ]
} ],
"nodes" : [ {
"name" : "publicationsystem:attachmentResource",
"primaryType" : "publicationsystem:resource",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "jcr:encoding",
"type" : "STRING",
"multiple" : false,
"values" : [ "${file.jrcEncoding}" ]
}, {
"name" : "jcr:lastModified",
"type" : "DATE",
"multiple" : false,
"values" : [ "${file.lastModified}" ]
}, {
"name" : "jcr:data",
"type" : "BINARY",
"multiple" : false,
"values" : [ "${file.data}" ]
}, {
"name" : "jcr:mimeType",
"type" : "STRING",
"multiple" : false,
"values" : [ "text/csv" ]
}, {
"name" : "hippo:filename",
"type" : "STRING",
"multiple" : false,
"values" : [ "${file.fileName}" ]
} ],
"nodes" : [ ]
} ]
}<#sep>, </#sep></#list></#if>
]}