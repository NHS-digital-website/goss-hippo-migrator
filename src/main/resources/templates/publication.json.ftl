<#-- @ftlvariable name="publication" type="uk.nhs.digital.ps.migrator.model.hippo.Publication" -->
{
"name" : "${publication.jcrNodeName}",
"primaryType" : "publicationsystem:publication",
"mixinTypes" : [ "mix:referenceable", "hippotaxonomy:classifiable" ],
"properties" : [ {
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
"name" : "publicationsystem:Summary",
"type" : "STRING",
"multiple" : false,
"values" : [ "${publication.summary}" ]
}<#if publication.coverageEnd?has_content>, {
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
}</#if>, {
"name" : "publicationsystem:PubliclyAccessible",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "true" ]
}, {
"name" : "publicationsystem:AdministrativeSources",
"type" : "STRING",
"multiple" : false,
"values" : [ "" ]
}, {<#-- This should change to rich text soon -->
"name" : "publicationsystem:KeyFacts",
"type" : "STRING",
"multiple" : false,
"values" : [ "${publication.keyFactsString}" ]
}, {
"name" : "hippotranslation:locale",
"type" : "STRING",
"multiple" : false,
"values" : [ "en" ]
}<#if publication.taxonomyKeys?has_content>, {
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
"nodes" : [<#if publication.relatedLinks?has_content><#list publication.relatedLinks as relatedLink>
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
}<#sep>, </#sep></#list><#if publication.resourceLinks?has_content || publication.files?has_content>, </#if>
</#if>
<#if publication.resourceLinks?has_content><#list publication.resourceLinks as resourceLink>
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
}<#sep>, </#sep></#list><#if publication.files?has_content>, </#if>
</#if>
<#if publication.files?has_content><#list publication.files as file>
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

<#--  Key facts should be rich text.  Currently String in doc type in hippo. <#if publication.keyFacts??>{
"name" : "publicationsystem:KeyFacts",
"primaryType" : "hippostd:html",
"mixinTypes" : [ ],
"properties" : [ {
"name" : "hippostd:content",
"type" : "STRING",
"multiple" : false,
"values" : [ "${publication.keyFacts.content}" ]
} ]
}</#if>
End commenting out key facts-->
]
}
