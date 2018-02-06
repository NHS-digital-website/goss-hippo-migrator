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
  }, {
    "name" : "publicationsystem:CoverageEnd",
    "type" : "DATE",
    "multiple" : false,
    "values" : [ "${publication.coverageEnd}" ]
  }, {
    "name" : "publicationsystem:NominalDate",
    "type" : "DATE",
    "multiple" : false,
    "values" : [ "${publication.publicationDate}" ]
  }, {
    "name" : "publicationsystem:CoverageStart",
    "type" : "DATE",
    "multiple" : false,
    "values" : [ "${publication.coverageStart}" ]
  }, {
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
  } ],
  <#--
  The nodes array will hold rich text components and complex components.
  If will contain one node for each top task, one or no node for introduction
  and a node per each section.  Nodes separated by commas that are suppressed
  if the preceding nodes did not exist.  This relies on the preceding nodes
  bean objects being null.
  -->
  "nodes" : [<#--  Key facts should be rich text.  Currently String in doc type in hippo. <#if publication.keyFacts??>{
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
  End commenting out key facts-->]
}
