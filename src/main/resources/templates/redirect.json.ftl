<#-- @ftlvariable name="redirect" type="uk.nhs.digital.gossmigrator.model.hippo.Redirect" -->
{
"name" : "${redirect.jcrNodeName}",
"primaryType" : "urlrewriter:advancedrule",
"mixinTypes" : [ "mix:referenceable" ],
"properties" : [{
"name" : "urlrewriter:ruleto",
"type" : "STRING",
"multiple" : false,
"values" : [ "${redirect.ruleTo}" ]
}, {
"name" : "urlrewriter:ruletype",
"type" : "STRING",
"multiple" : false,
"values" : [ "temporary-redirect" ]
}, {
"name" : "hippo:availability",
"type" : "STRING",
"multiple" : true,
"values" : [ "live" ]
}, {
"name" : "urlrewriter:rulefrom",
"type" : "STRING",
"multiple" : false,
"values" : [ "${redirect.ruleFrom}"]
}, {
"name" : "urlrewriter:ruledescription",
"type" : "STRING",
"multiple" : false,
"values" : [ "${redirect.description}" ]
}, {
"name" : "urlrewriter:isnotlast",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "true" ]
}, {
"name" : "urlrewriter:iswildcardtype",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "false" ]
}, {
"name" : "urlrewriter:qsappend",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "false" ]
}, {
"name" : "urlrewriter:casesensitive",
"type" : "BOOLEAN",
"multiple" : false,
"values" : [ "false" ]
},{
"name" : "jcr:path",
"type" : "STRING",
"multiple" : false,
"values" : [ "${redirect.jcrPath}" ]
},{
"name" : "jcr:localizedName",
"type" : "STRING",
"multiple" : false,
"values" : [ "${redirect.jcrNodeName}" ]
}],
"nodes" : [ ]
}