import{i as De,g as ee,b as he,c as Q,d as we,e as Ce}from"./axios.6f786f47.js";import{X as Se,Z as Te,r as m,e as te,f as me,g as C,o as R,v as F,w as v,i as d,l as $,u as re,$ as pe,G as ne,F as z,b as fe,a as L,x as ve,k as X,c as J,K as Pe,a0 as ke,q as xe,D as Ie,U as Ve,y as Me,V as Re,p as Be,m as Ne}from"./index.a06947c9.js";import{i as A}from"./types.22d094ed.js";import{u as $e,a as Ue}from"./function.42cfce21.js";import{a as Ae}from"./sdk.3607da68.js";import{a as Le,b as ge}from"./bpmn.233e1eb0.js";import{u as oe}from"./index.6a0f63a4.js";import{S as Fe,s as Ge}from"./field.a48e9838.js";import"./index.203e7cb0.js";const q=Se("bpmn",{state:()=>({nodeId:null,selectDialogVisible:!1,editParamsDialogVisible:!1,bpmnInstance:{}}),getters:{},actions:{SetBpmnInstance(s){this.bpmnInstance=s},SetSelectDialogVisible(s){this.selectDialogVisible=s},SetNodeInfo(s){this.nodeId=Te(s)},SetParamsDialogVisible(s){this.editParamsDialogVisible=s}}});function le(s,c){const a=s==null?void 0:s.get("extensionElements");if(!a)return[];const e=a.get("values");return!e||!e.length?[]:c?e.filter(i=>De(i,c)):e}function se(s,c,a,e){const i=e.create(s,c);return a&&(i.$parent=a),i}function Oe(s,c){return`<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
  targetNamespace="http://bpmn.io/schema/bpmn"
  id="Definitions_0001">
  <bpmn:process id="${s}" name="${c}" isExecutable="true" />
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="${s}" />
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`}async function ce(s,c,a){try{const e=Date.now(),{processId:i,processName:E}=a||{},D=i||`Process_${e}`,g=E||`\u4E1A\u52A1\u6D41\u7A0B_${e}`,b=c||Oe(D,g),{warnings:y}=await s.importXML(b);y&&y.length&&console.warnings(y)}catch(e){console.error(e)}}function ue(s,c,a){const e=encodeURIComponent(a);return{filename:`${c}.${s.toLowerCase()}`,href:`data:application/${s==="svg"?"text/xml":"bpmn20-xml"};charset=UTF-8,${e}`,data:a}}function de(s,c){if(s&&c){const a=document.createElement("a");a.download=c,a.href=s,a.click(),URL.revokeObjectURL(a.href)}}const Y=q();class be{constructor(c,a,e,i,E,D,g,b){this.create=e,this.elementFactory=i,this.translate=D,this.modeling=g,this.bpmnFactory=b,c.autoPlace!==!1&&(this.autoPlace=E.get("autoPlace",!1)),a.registerProvider(this)}getContextPadEntries(c){const{translate:a}=this;function e(){Y.SetNodeInfo(c.id),Y.SetSelectDialogVisible(!0)}function i(){Y.SetNodeInfo(c.id),Y.SetParamsDialogVisible(!0)}function E(){return{group:"edit",className:"icon-custom select-data",title:a("\u7F16\u8F91"),action:{click:e}}}function D(){return{group:"edit",className:"icon-custom edit-params",title:a("\u7F16\u8F91\u53C2\u6570\u6620\u5C04"),action:{click:i}}}return{edit:E(),edit2:D()}}}be.$inject=["config","contextPad","create","elementFactory","injector","translate","modeling","bpmnFactory"];var Xe={__init__:["customContextPad"],customContextPad:["type",be]},je={Id:"\u7F16\u53F7",Name:"\u540D\u79F0",General:"\u5E38\u89C4",Details:"\u8BE6\u60C5","Message Name":"\u6D88\u606F\u540D\u79F0",Message:"\u6D88\u606F",Initiator:"\u521B\u5EFA\u8005","Asynchronous continuations":"\u6301\u7EED\u5F02\u6B65","Asynchronous before":"\u5F02\u6B65\u524D","Asynchronous after":"\u5F02\u6B65\u540E","Job configuration":"\u5DE5\u4F5C\u914D\u7F6E",Exclusive:"\u6392\u9664","Job Priority":"\u5DE5\u4F5C\u4F18\u5148\u7EA7","Retry Time Cycle":"\u91CD\u8BD5\u65F6\u95F4\u5468\u671F",Documentation:"\u6587\u6863","Element Documentation":"\u5143\u7D20\u6587\u6863","History Configuration":"\u5386\u53F2\u914D\u7F6E","History Time To Live":"\u5386\u53F2\u7684\u751F\u5B58\u65F6\u95F4",Forms:"\u8868\u5355","Activate the global connect tool":"\u6FC0\u6D3B\u5168\u5C40\u8FDE\u63A5\u5DE5\u5177","Append {type}":"\u8FFD\u52A0 {type}","Append EndEvent":"\u8FFD\u52A0 \u7ED3\u675F\u4E8B\u4EF6 ","Append Task":"\u8FFD\u52A0 \u4EFB\u52A1","Append Gateway":"\u8FFD\u52A0 \u7F51\u5173","Append Intermediate/Boundary Event":"\u8FFD\u52A0 \u4E2D\u95F4/\u8FB9\u754C \u4E8B\u4EF6","Add Lane above":"\u5728\u4E0A\u9762\u6DFB\u52A0\u9053","Divide into two Lanes":"\u5206\u5272\u6210\u4E24\u4E2A\u9053","Divide into three Lanes":"\u5206\u5272\u6210\u4E09\u4E2A\u9053","Add Lane below":"\u5728\u4E0B\u9762\u6DFB\u52A0\u9053","Append compensation activity":"\u8FFD\u52A0\u8865\u507F\u6D3B\u52A8","Change type":"\u4FEE\u6539\u7C7B\u578B","Connect using Association":"\u4F7F\u7528\u5173\u8054\u8FDE\u63A5","Connect using Sequence/MessageFlow or Association":"\u4F7F\u7528\u987A\u5E8F/\u6D88\u606F\u6D41\u6216\u8005\u5173\u8054\u8FDE\u63A5","Connect using DataInputAssociation":"\u4F7F\u7528\u6570\u636E\u8F93\u5165\u5173\u8054\u8FDE\u63A5",Remove:"\u79FB\u9664","Activate the hand tool":"\u6FC0\u6D3B\u6293\u624B\u5DE5\u5177","Activate the lasso tool":"\u6FC0\u6D3B\u5957\u7D22\u5DE5\u5177","Activate the create/remove space tool":"\u6FC0\u6D3B\u521B\u5EFA/\u5220\u9664\u7A7A\u95F4\u5DE5\u5177","Create expanded SubProcess":"\u521B\u5EFA\u6269\u5C55\u5B50\u8FC7\u7A0B","Create IntermediateThrowEvent/BoundaryEvent":"\u521B\u5EFA\u4E2D\u95F4\u629B\u51FA\u4E8B\u4EF6/\u8FB9\u754C\u4E8B\u4EF6","Create Pool/Participant":"\u521B\u5EFA\u6C60/\u53C2\u4E0E\u8005","Parallel Multi Instance":"\u5E76\u884C\u591A\u91CD\u4E8B\u4EF6","Sequential Multi Instance":"\u65F6\u5E8F\u591A\u91CD\u4E8B\u4EF6",DataObjectReference:"\u6570\u636E\u5BF9\u8C61\u53C2\u8003",DataStoreReference:"\u6570\u636E\u5B58\u50A8\u53C2\u8003",Loop:"\u5FAA\u73AF","Ad-hoc":"\u5373\u5E2D","Create {type}":"\u521B\u5EFA {type}","Create Task":"\u521B\u5EFA\u4EFB\u52A1","Create StartEvent":"\u521B\u5EFA\u5F00\u59CB\u4E8B\u4EF6","Create EndEvent":"\u521B\u5EFA\u7ED3\u675F\u4E8B\u4EF6","Create Group":"\u521B\u5EFA\u7EC4",Task:"\u4EFB\u52A1","Send Task":"\u53D1\u9001\u4EFB\u52A1","Receive Task":"\u63A5\u6536\u4EFB\u52A1","User Task":"\u7528\u6237\u4EFB\u52A1","Manual Task":"\u624B\u5DE5\u4EFB\u52A1","Business Rule Task":"\u4E1A\u52A1\u89C4\u5219\u4EFB\u52A1","Service Task":"\u670D\u52A1\u4EFB\u52A1","Script Task":"\u811A\u672C\u4EFB\u52A1","Call Activity":"\u8C03\u7528\u6D3B\u52A8","Sub Process (collapsed)":"\u5B50\u6D41\u7A0B\uFF08\u6298\u53E0\u7684\uFF09","Sub Process (expanded)":"\u5B50\u6D41\u7A0B\uFF08\u5C55\u5F00\u7684\uFF09","Start Event":"\u5F00\u59CB\u4E8B\u4EF6",StartEvent:"\u5F00\u59CB\u4E8B\u4EF6","Intermediate Throw Event":"\u4E2D\u95F4\u4E8B\u4EF6","End Event":"\u7ED3\u675F\u4E8B\u4EF6",EndEvent:"\u7ED3\u675F\u4E8B\u4EF6","Create Gateway":"\u521B\u5EFA\u7F51\u5173",GateWay:"\u7F51\u5173","Create Intermediate/Boundary Event":"\u521B\u5EFA\u4E2D\u95F4/\u8FB9\u754C\u4E8B\u4EF6","Message Start Event":"\u6D88\u606F\u5F00\u59CB\u4E8B\u4EF6","Timer Start Event":"\u5B9A\u65F6\u5F00\u59CB\u4E8B\u4EF6","Conditional Start Event":"\u6761\u4EF6\u5F00\u59CB\u4E8B\u4EF6","Signal Start Event":"\u4FE1\u53F7\u5F00\u59CB\u4E8B\u4EF6","Error Start Event":"\u9519\u8BEF\u5F00\u59CB\u4E8B\u4EF6","Escalation Start Event":"\u5347\u7EA7\u5F00\u59CB\u4E8B\u4EF6","Compensation Start Event":"\u8865\u507F\u5F00\u59CB\u4E8B\u4EF6","Message Start Event (non-interrupting)":"\u6D88\u606F\u5F00\u59CB\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Timer Start Event (non-interrupting)":"\u5B9A\u65F6\u5F00\u59CB\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Conditional Start Event (non-interrupting)":"\u6761\u4EF6\u5F00\u59CB\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Signal Start Event (non-interrupting)":"\u4FE1\u53F7\u5F00\u59CB\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Escalation Start Event (non-interrupting)":"\u5347\u7EA7\u5F00\u59CB\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Message Intermediate Catch Event":"\u6D88\u606F\u4E2D\u95F4\u6355\u83B7\u4E8B\u4EF6","Message Intermediate Throw Event":"\u6D88\u606F\u4E2D\u95F4\u629B\u51FA\u4E8B\u4EF6","Timer Intermediate Catch Event":"\u5B9A\u65F6\u4E2D\u95F4\u6355\u83B7\u4E8B\u4EF6","Escalation Intermediate Throw Event":"\u5347\u7EA7\u4E2D\u95F4\u629B\u51FA\u4E8B\u4EF6","Conditional Intermediate Catch Event":"\u6761\u4EF6\u4E2D\u95F4\u6355\u83B7\u4E8B\u4EF6","Link Intermediate Catch Event":"\u94FE\u63A5\u4E2D\u95F4\u6355\u83B7\u4E8B\u4EF6","Link Intermediate Throw Event":"\u94FE\u63A5\u4E2D\u95F4\u629B\u51FA\u4E8B\u4EF6","Compensation Intermediate Throw Event":"\u8865\u507F\u4E2D\u95F4\u629B\u51FA\u4E8B\u4EF6","Signal Intermediate Catch Event":"\u4FE1\u53F7\u4E2D\u95F4\u6355\u83B7\u4E8B\u4EF6","Signal Intermediate Throw Event":"\u4FE1\u53F7\u4E2D\u95F4\u629B\u51FA\u4E8B\u4EF6","Message End Event":"\u6D88\u606F\u7ED3\u675F\u4E8B\u4EF6","Escalation End Event":"\u5B9A\u65F6\u7ED3\u675F\u4E8B\u4EF6","Error End Event":"\u9519\u8BEF\u7ED3\u675F\u4E8B\u4EF6","Cancel End Event":"\u53D6\u6D88\u7ED3\u675F\u4E8B\u4EF6","Compensation End Event":"\u8865\u507F\u7ED3\u675F\u4E8B\u4EF6","Signal End Event":"\u4FE1\u53F7\u7ED3\u675F\u4E8B\u4EF6","Terminate End Event":"\u7EC8\u6B62\u7ED3\u675F\u4E8B\u4EF6","Message Boundary Event":"\u6D88\u606F\u8FB9\u754C\u4E8B\u4EF6","Message Boundary Event (non-interrupting)":"\u6D88\u606F\u8FB9\u754C\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Timer Boundary Event":"\u5B9A\u65F6\u8FB9\u754C\u4E8B\u4EF6","Timer Boundary Event (non-interrupting)":"\u5B9A\u65F6\u8FB9\u754C\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Escalation Boundary Event":"\u5347\u7EA7\u8FB9\u754C\u4E8B\u4EF6","Escalation Boundary Event (non-interrupting)":"\u5347\u7EA7\u8FB9\u754C\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Conditional Boundary Event":"\u6761\u4EF6\u8FB9\u754C\u4E8B\u4EF6","Conditional Boundary Event (non-interrupting)":"\u6761\u4EF6\u8FB9\u754C\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Error Boundary Event":"\u9519\u8BEF\u8FB9\u754C\u4E8B\u4EF6","Cancel Boundary Event":"\u53D6\u6D88\u8FB9\u754C\u4E8B\u4EF6","Signal Boundary Event":"\u4FE1\u53F7\u8FB9\u754C\u4E8B\u4EF6","Signal Boundary Event (non-interrupting)":"\u4FE1\u53F7\u8FB9\u754C\u4E8B\u4EF6\uFF08\u975E\u4E2D\u65AD\uFF09","Compensation Boundary Event":"\u8865\u507F\u8FB9\u754C\u4E8B\u4EF6","Exclusive Gateway":"\u4E92\u65A5\u7F51\u5173","Parallel Gateway":"\u5E76\u884C\u7F51\u5173","Inclusive Gateway":"\u76F8\u5BB9\u7F51\u5173","Complex Gateway":"\u590D\u6742\u7F51\u5173","Event based Gateway":"\u4E8B\u4EF6\u7F51\u5173",Transaction:"\u8F6C\u8FD0","Sub Process":"\u5B50\u6D41\u7A0B","Event Sub Process":"\u4E8B\u4EF6\u5B50\u6D41\u7A0B","Collapsed Pool":"\u6298\u53E0\u6C60","Expanded Pool":"\u5C55\u5F00\u6C60","no parent for {element} in {parent}":"\u5728{parent}\u91CC\uFF0C{element}\u6CA1\u6709\u7236\u7C7B","no shape type specified":"\u6CA1\u6709\u6307\u5B9A\u7684\u5F62\u72B6\u7C7B\u578B","flow elements must be children of pools/participants":"\u6D41\u5143\u7D20\u5FC5\u987B\u662F\u6C60/\u53C2\u4E0E\u8005\u7684\u5B50\u7C7B","out of bounds release":"out of bounds release","more than {count} child lanes":"\u5B50\u9053\u5927\u4E8E{count} ","element required":"\u5143\u7D20\u4E0D\u80FD\u4E3A\u7A7A","diagram not part of bpmn:Definitions":"\u6D41\u7A0B\u56FE\u4E0D\u7B26\u5408bpmn\u89C4\u8303","no diagram to display":"\u6CA1\u6709\u53EF\u5C55\u793A\u7684\u6D41\u7A0B\u56FE","no process or collaboration to display":"\u6CA1\u6709\u53EF\u5C55\u793A\u7684\u6D41\u7A0B/\u534F\u4F5C","element {element} referenced by {referenced}#{property} not yet drawn":"\u7531{referenced}#{property}\u5F15\u7528\u7684{element}\u5143\u7D20\u4ECD\u672A\u7ED8\u5236","already rendered {element}":"{element} \u5DF2\u88AB\u6E32\u67D3","failed to import {element}":"\u5BFC\u5165{element}\u5931\u8D25",Id:"\u7F16\u53F7",Name:"\u540D\u79F0",General:"\u5E38\u89C4",Details:"\u8BE6\u60C5","Message Name":"\u6D88\u606F\u540D\u79F0",Message:"\u6D88\u606F",Initiator:"\u521B\u5EFA\u8005","Asynchronous Continuations":"\u6301\u7EED\u5F02\u6B65","Asynchronous Before":"\u5F02\u6B65\u524D","Asynchronous After":"\u5F02\u6B65\u540E","Job Configuration":"\u5DE5\u4F5C\u914D\u7F6E",Exclusive:"\u6392\u9664","Job Priority":"\u5DE5\u4F5C\u4F18\u5148\u7EA7","Retry Time Cycle":"\u91CD\u8BD5\u65F6\u95F4\u5468\u671F",Documentation:"\u6587\u6863","Element Documentation":"\u5143\u7D20\u6587\u6863","History Configuration":"\u5386\u53F2\u914D\u7F6E","History Time To Live":"\u5386\u53F2\u7684\u751F\u5B58\u65F6\u95F4",Forms:"\u8868\u5355","Form Key":"\u8868\u5355key","Form Fields":"\u8868\u5355\u5B57\u6BB5","Business Key":"\u4E1A\u52A1key","Form Field":"\u8868\u5355\u5B57\u6BB5",ID:"\u7F16\u53F7",Type:"\u7C7B\u578B",Label:"\u540D\u79F0","Default Value":"\u9ED8\u8BA4\u503C",Validation:"\u6821\u9A8C","Add Constraint":"\u6DFB\u52A0\u7EA6\u675F",Config:"\u914D\u7F6E",Properties:"\u5C5E\u6027","Add Property":"\u6DFB\u52A0\u5C5E\u6027",Value:"\u503C",Add:"\u6DFB\u52A0",Values:"\u503C","Add Value":"\u6DFB\u52A0\u503C",Listeners:"\u76D1\u542C\u5668","Execution Listener":"\u6267\u884C\u76D1\u542C","Event Type":"\u4E8B\u4EF6\u7C7B\u578B","Listener Type":"\u76D1\u542C\u5668\u7C7B\u578B","Java Class":"Java\u7C7B",Expression:"\u8868\u8FBE\u5F0F","Must provide a value":"\u5FC5\u987B\u63D0\u4F9B\u4E00\u4E2A\u503C","Delegate Expression":"\u4EE3\u7406\u8868\u8FBE\u5F0F",Script:"\u811A\u672C","Script Format":"\u811A\u672C\u683C\u5F0F","Script Type":"\u811A\u672C\u7C7B\u578B","Inline Script":"\u5185\u8054\u811A\u672C","External Script":"\u5916\u90E8\u811A\u672C",Resource:"\u8D44\u6E90","Field Injection":"\u5B57\u6BB5\u6CE8\u5165",Extensions:"\u6269\u5C55","Input/Output":"\u8F93\u5165/\u8F93\u51FA","Input Parameters":"\u8F93\u5165\u53C2\u6570","Output Parameters":"\u8F93\u51FA\u53C2\u6570",Parameters:"\u53C2\u6570","Output Parameter":"\u8F93\u51FA\u53C2\u6570","Timer Definition Type":"\u5B9A\u65F6\u5668\u5B9A\u4E49\u7C7B\u578B","Timer Definition":"\u5B9A\u65F6\u5668\u5B9A\u4E49",Date:"\u65E5\u671F",Duration:"\u6301\u7EED",Cycle:"\u5FAA\u73AF",Signal:"\u4FE1\u53F7","Signal Name":"\u4FE1\u53F7\u540D\u79F0",Escalation:"\u5347\u7EA7",Error:"\u9519\u8BEF","Link Name":"\u94FE\u63A5\u540D\u79F0",Condition:"\u6761\u4EF6\u540D\u79F0","Variable Name":"\u53D8\u91CF\u540D\u79F0","Variable Event":"\u53D8\u91CF\u4E8B\u4EF6","Specify more than one variable change event as a comma separated list.":"\u591A\u4E2A\u53D8\u91CF\u4E8B\u4EF6\u4EE5\u9017\u53F7\u9694\u5F00","Wait for Completion":"\u7B49\u5F85\u5B8C\u6210","Activity Ref":"\u6D3B\u52A8\u53C2\u8003","Version Tag":"\u7248\u672C\u6807\u7B7E",Executable:"\u53EF\u6267\u884C\u6587\u4EF6","External Task Configuration":"\u6269\u5C55\u4EFB\u52A1\u914D\u7F6E","Task Priority":"\u4EFB\u52A1\u4F18\u5148\u7EA7",External:"\u5916\u90E8",Connector:"\u8FDE\u63A5\u5668","Must configure Connector":"\u5FC5\u987B\u914D\u7F6E\u8FDE\u63A5\u5668","Connector Id":"\u8FDE\u63A5\u5668\u7F16\u53F7",Implementation:"\u5B9E\u73B0\u65B9\u5F0F","Field Injections":"\u5B57\u6BB5\u6CE8\u5165",Fields:"\u5B57\u6BB5","Result Variable":"\u7ED3\u679C\u53D8\u91CF",Topic:"\u4E3B\u9898","Configure Connector":"\u914D\u7F6E\u8FDE\u63A5\u5668","Input Parameter":"\u8F93\u5165\u53C2\u6570",Assignee:"\u4EE3\u7406\u4EBA","Candidate Users":"\u5019\u9009\u7528\u6237","Candidate Groups":"\u5019\u9009\u7EC4","Due Date":"\u5230\u671F\u65F6\u95F4","Follow Up Date":"\u8DDF\u8E2A\u65E5\u671F",Priority:"\u4F18\u5148\u7EA7","The follow up date as an EL expression (e.g. ${someDate} or an ISO date (e.g. 2015-06-26T09:54:00)":"\u8DDF\u8E2A\u65E5\u671F\u5FC5\u987B\u7B26\u5408EL\u8868\u8FBE\u5F0F\uFF0C\u5982\uFF1A ${someDate} ,\u6216\u8005\u4E00\u4E2AISO\u6807\u51C6\u65E5\u671F\uFF0C\u5982\uFF1A2015-06-26T09:54:00","The due date as an EL expression (e.g. ${someDate} or an ISO date (e.g. 2015-06-26T09:54:00)":"\u8DDF\u8E2A\u65E5\u671F\u5FC5\u987B\u7B26\u5408EL\u8868\u8FBE\u5F0F\uFF0C\u5982\uFF1A ${someDate} ,\u6216\u8005\u4E00\u4E2AISO\u6807\u51C6\u65E5\u671F\uFF0C\u5982\uFF1A2015-06-26T09:54:00",Variables:"\u53D8\u91CF","Candidate Starter Configuration":"\u5019\u9009\u5F00\u59CB\u914D\u7F6E","Task Listener":"\u4EFB\u52A1\u76D1\u542C\u5668","Candidate Starter Groups":"\u5019\u9009\u5F00\u59CB\u7EC4","Candidate Starter Users":"\u5019\u9009\u5F00\u59CB\u7528\u6237","Tasklist Configuration":"\u4EFB\u52A1\u5217\u8868\u914D\u7F6E",Startable:"\u542F\u52A8","Specify more than one group as a comma separated list.":"\u6307\u5B9A\u591A\u4E2A\u7EC4,\u7528\u9017\u53F7\u5206\u9694","Specify more than one user as a comma separated list.":"\u6307\u5B9A\u591A\u4E2A\u7528\u6237,\u7528\u9017\u53F7\u5206\u9694","This maps to the process definition key.":"\u8FD9\u4F1A\u6620\u5C04\u4E3A\u6D41\u7A0B\u5B9A\u4E49\u7684\u952E","CallActivity Type":"\u8C03\u7528\u6D3B\u52A8\u7C7B\u578B","Condition Type":"\u6761\u4EF6\u7C7B\u578B","Create UserTask":"\u521B\u5EFA\u7528\u6237\u4EFB\u52A1","Create CallActivity":"\u521B\u5EFA\u8C03\u7528\u6D3B\u52A8","Called Element":"\u8C03\u7528\u5143\u7D20","Create DataObjectReference":"\u521B\u5EFA\u6570\u636E\u5BF9\u8C61\u5F15\u7528","Create DataStoreReference":"\u521B\u5EFA\u6570\u636E\u5B58\u50A8\u5F15\u7528","Multi Instance":"\u591A\u5B9E\u4F8B","Loop Cardinality":"\u5B9E\u4F8B\u6570\u91CF",Collection:"\u4EFB\u52A1\u53C2\u4E0E\u4EBA\u5217\u8868","Element Variable":"\u5143\u7D20\u53D8\u91CF","Completion Condition":"\u5B8C\u6210\u6761\u4EF6"};const K=(s={})=>({prop:"order",label:"\u5E8F\u53F7",type:"index",width:60,...s}),H=(s={})=>({prop:"action",label:"\u64CD\u4F5C",width:"180px",fixed:"right",...s}),N=(s,c,a={})=>typeof s=="string"?{prop:s,label:c,...a}:typeof s=="object"?s:{};var We={__name:"selectDialog",emits:["success"],setup(s,{expose:c,emit:a}){const e=m({background:!0,page:1,size:10,layout:"prev, pager, next",total:0}),i=m({status:Fe.OPEN}),E=m([oe("areaName","\u9886\u57DF\u540D\u79F0"),oe("name","\u57DF\u529F\u80FD\u540D\u79F0")]),D=m([K(),N("name","\u57DF\u529F\u80FD\u540D\u79F0"),N("taskComponent","\u8282\u70B9\u7EC4\u4EF6"),N("taskService","\u8282\u70B9\u670D\u52A1"),N("serviceTaskType","\u8282\u70B9\u7C7B\u578B",{render:({row:o})=>Ge[o.serviceTaskType]}),oe("areaName","\u9886\u57DF\u540D\u79F0"),H()]),g=async({searchData:o})=>{const{nodes:u}=await Le({page:1,size:100,...o});return{records:u,total:100}},b=q(),y=te(()=>b.selectDialogVisible),T=async o=>{b.SetSelectDialogVisible(!1),ne({message:"\u606D\u559C\u4F60\uFF0C\u64CD\u4F5C\u6210\u529F",type:"success"}),a("success",o)},t=()=>{b.SetSelectDialogVisible(!1)};return c({}),me(()=>{b.SetSelectDialogVisible(!1)}),(o,u)=>{const h=C("el-button"),V=C("yun-pro-table"),n=C("yun-drawer");return R(),F(n,{modelValue:re(y),"onUpdate:modelValue":u[2]||(u[2]=p=>pe(y)?y.value=p:null),size:"large",title:"\u9009\u62E9\u57DF\u529F\u80FD","show-confirm-button":!1,"cancel-button-text":"\u5173\u95ED",onConfirm:T,onClosed:t},{default:v(()=>[d(V,{ref:"proTableRef",searchData:i.value,"onUpdate:searchData":u[0]||(u[0]=p=>i.value=p),pagination:e.value,"onUpdate:pagination":u[1]||(u[1]=p=>e.value=p),"search-fields":E.value,"table-columns":D.value,"remote-method":g},{t_action:v(({row:p})=>[d(h,{type:"action",onClick:P=>T(p)},{default:v(()=>[$(" \u9009\u62E9 ")]),_:2},1032,["onClick"])]),_:1},8,["searchData","pagination","search-fields","table-columns"])]),_:1},8,["modelValue"])}}},Je={__name:"saveDialog",emits:["success"],setup(s,{expose:c,emit:a}){const e=m(!1),i=m({startEventId:void 0}),E=m([{label:"\u8D77\u59CBid",prop:"startEventId"},{label:"bpmn\u7684Url",prop:"bpmnUrl",type:"input",render:()=>d(z,null,[d(C("el-row"),{align:"middle",justify:"space-between"},{default:()=>[!A(i.value.bpmnUrl)&&d("div",{class:"sdk-url-text "},[d(C("yun-ellipsis"),{text:i.value.bpmnUrl,"visible-line":2},null)])]})])},{label:"bpmn\u540D\u79F0",prop:"bpmnName",type:"input"}]),D=y=>{e.value=!0,i.value=y},g=async y=>{await $e(i.value),ne({message:"\u606D\u559C\u4F60\uFF0C\u64CD\u4F5C\u6210\u529F",type:"success"}),e.value=!1,a("success",y)},b=()=>{i.value={},e.value=!1};return me(()=>{e.value=!1}),c({show:D}),(y,T)=>{const t=C("yun-pro-form"),o=C("yun-drawer");return R(),F(o,{modelValue:e.value,"onUpdate:modelValue":T[0]||(T[0]=u=>e.value=u),size:"large",title:"\u63D0\u4EA4\u4FDD\u5B58",onConfirm:g,onClosed:b},{default:v(()=>[d(t,{ref:"formRef",form:i.value,columns:E.value,"form-props":{labelWidth:135},config:{colProps:{span:24}}},null,8,["form","columns"])]),_:1},8,["modelValue"])}}};const qe=["onClick"],ze={__name:"selectSpecificParamsDrawer",emits:"done",setup(s,{expose:c,emit:a}){const e=q(),i=te(()=>e.bpmnInstance),E=m([K(),N("targetName","targetName"),H({width:120})]),D=m({}),g=m([]),b=m(!1);function y(){b.value=!1,g.value=[]}function T(u){a("done",{scope:D.value,row:u}),y()}async function t(){var x,S,M;const h=i.value.get("elementRegistry").getAll()[0].children.find(({id:I})=>I===e.nodeId),V=ee(h),n=le(V)[0],p=(x=n.values.find(({name:I})=>I==="task-component"))==null?void 0:x.value,P=(S=n.values.find(({name:I})=>I==="task-service"))==null?void 0:S.value,G=await ge({taskComponent:p,taskService:P}),{nodes:j}=G,{paramMapping:k}=j[0]||{},_=((M=k==null?void 0:k.map)==null?void 0:M.nodeParamConfigs)||[];return{records:_,total:_.length}}function o(u){D.value=u,b.value=!0}return c({show:o}),(u,h)=>{const V=C("el-button"),n=C("yun-pro-table"),p=C("yun-drawer");return R(),F(p,{modelValue:b.value,"onUpdate:modelValue":h[1]||(h[1]=P=>b.value=P),size:"large",title:"\u6807\u9898","show-confirm-button":!1,"cancel-button-text":"\u5173 \u95ED",onClosed:y},{default:v(()=>[b.value?(R(),F(n,{key:0,ref:"proTableRef",tableData:g.value,"onUpdate:tableData":h[0]||(h[0]=P=>g.value=P),"table-columns":E.value,"remote-method":t,"default-fetch":!0},{t_action:v(({row:P})=>[d(V,{type:"action",onClick:G=>T(P)},{default:v(()=>[$(" \u9009\u62E9 ")]),_:2},1032,["onClick"])]),t_targetName:v(({row:P})=>[L("span",{class:"targetName",onClick:G=>T(P)},ve(P.targetName),9,qe)]),_:1},8,["tableData","table-columns"])):X("v-if",!0)]),_:1},8,["modelValue"])}}};var ie=fe(ze,[["__scopeId","data-v-74c03920"]]);const Ke={class:"flex justify-between"},He={__name:"fieldMappingTable",setup(s,{expose:c}){const a=["task-component","task-service","service_task_type","delay_time"],e=m([]),i=m(),E=m([K(),N("name","name"),N("value","value"),H()]);function D(){e.value.push({})}function g({scope:t,row:o}){t.row.name=o.targetName}function b(t,o){const{$index:u}=o;this.list.splice(u,1)}function y(t){e.value=t}function T(){return e.value.filter(({name:t,value:o})=>!A(t)||!A(o))}return c({setTableData:y,getTableData:T}),(t,o)=>{const u=C("el-button"),h=C("el-input"),V=C("yun-pro-table");return R(),J(z,null,[d(V,{ref:"proTableRef",tableData:e.value,"onUpdate:tableData":o[0]||(o[0]=n=>e.value=n),"table-columns":E.value},{tableHeaderLeft:v(()=>[d(u,{type:"primary",onClick:D},{default:v(()=>[$(" \u65B0\u589E ")]),_:1})]),t_action:v(({row:n,...p})=>[a.includes(n.name)?X("v-if",!0):(R(),F(u,{key:0,type:"action",onClick:P=>b(n,p)},{default:v(()=>[$(" \u5220\u9664 ")]),_:2},1032,["onClick"]))]),t_name:v(n=>[L("div",Ke,[d(h,{modelValue:n.row.name,"onUpdate:modelValue":p=>n.row.name=p,modelModifiers:{trim:!0},style:{width:"90%"},disabled:a.includes(n.row.name)},null,8,["modelValue","onUpdate:modelValue","disabled"]),X(` <el-button type="primary" @click="handleSelect(scope)">
          \u9009\u62E9
        </el-button> `)])]),t_value:v(n=>[d(h,{modelValue:n.row.value,"onUpdate:modelValue":p=>n.row.value=p,modelModifiers:{trim:!0},style:{width:"90%"},disabled:a.includes(n.row.name)},null,8,["modelValue","onUpdate:modelValue","disabled"])]),_:1},8,["tableData","table-columns"]),d(ie,{ref_key:"selectSpecificParamsDrawerRef",ref:i,onDone:g},null,512)],64)}}},Ze={__name:"appointParamTable",setup(s,{expose:c}){const a=["task-component","task-service","service_task_type","delay_time"],e=m([]),i=m(),E=m([K(),N("name","name"),N("value","value"),H()]);function D(){e.value.push({})}function g({scope:t,row:o}){t.row.value=o.value}function b(t,o){const{$index:u}=o;this.list.splice(u,1)}function y(t){e.value=t}function T(){return e.value.filter(({name:t,value:o})=>!A(t)||!A(o))}return c({setTableData:y,getTableData:T}),(t,o)=>{const u=C("el-button"),h=C("el-input"),V=C("yun-pro-table");return R(),J(z,null,[d(V,{ref:"proTableRef",tableData:e.value,"onUpdate:tableData":o[0]||(o[0]=n=>e.value=n),"table-columns":E.value},{tableHeaderLeft:v(()=>[d(u,{type:"primary",onClick:D},{default:v(()=>[$(" \u65B0\u589E ")]),_:1})]),t_action:v(({row:n,...p})=>[a.includes(n.name)?X("v-if",!0):(R(),F(u,{key:0,type:"action",onClick:P=>b(n,p)},{default:v(()=>[$(" \u5220\u9664 ")]),_:2},1032,["onClick"]))]),t_name:v(({row:n})=>[d(h,{modelValue:n.name,"onUpdate:modelValue":p=>n.name=p,modelModifiers:{trim:!0},style:{width:"90%"},disabled:a.includes(n.name)},null,8,["modelValue","onUpdate:modelValue","disabled"])]),t_value:v(n=>[d(h,{modelValue:n.row.value,"onUpdate:modelValue":p=>n.row.value=p,modelModifiers:{trim:!0},style:{width:"90%"},disabled:a.includes(n.row.name)},null,8,["modelValue","onUpdate:modelValue","disabled"])]),_:1},8,["tableData","table-columns"]),d(ie,{ref_key:"selectSpecificParamsDrawerRef",ref:i,onDone:g},null,512)],64)}}},Qe={__name:"conversionParamTable",setup(s,{expose:c}){const a=["task-component","task-service","service_task_type","delay_time"],e=m([]),i=m(),E=m([K(),N("name","name"),N("value","value"),H()]);function D(){e.value.push({})}function g({scope:t,row:o}){t.row.value=o.value}function b(t,o){const{$index:u}=o;this.list.splice(u,1)}function y(t){e.value=t}function T(){return e.value.filter(({name:t,value:o})=>!A(t)||!A(o))}return c({setTableData:y,getTableData:T}),(t,o)=>{const u=C("el-button"),h=C("el-input"),V=C("yun-pro-table");return R(),J(z,null,[d(V,{ref:"proTableRef",tableData:e.value,"onUpdate:tableData":o[0]||(o[0]=n=>e.value=n),"table-columns":E.value},{tableHeaderLeft:v(()=>[d(u,{type:"primary",onClick:D},{default:v(()=>[$(" \u65B0\u589E ")]),_:1})]),t_action:v(({row:n,...p})=>[a.includes(n.name)?X("v-if",!0):(R(),F(u,{key:0,type:"action",onClick:P=>b(n,p)},{default:v(()=>[$(" \u5220\u9664 ")]),_:2},1032,["onClick"]))]),t_name:v(({row:n})=>[d(h,{modelValue:n.name,"onUpdate:modelValue":p=>n.name=p,modelModifiers:{trim:!0},style:{width:"90%"},disabled:a.includes(n.name)},null,8,["modelValue","onUpdate:modelValue","disabled"])]),t_value:v(n=>[d(h,{modelValue:n.row.value,"onUpdate:modelValue":p=>n.row.value=p,modelModifiers:{trim:!0},style:{width:"90%"},disabled:a.includes(n.row.name)},null,8,["modelValue","onUpdate:modelValue","disabled"])]),_:1},8,["tableData","table-columns"]),d(ie,{ref_key:"selectSpecificParamsDrawerRef",ref:i,onDone:g},null,512)],64)}}},Ye={__name:"selectParamsDrawer",emits:"done",setup(s,{expose:c,emit:a}){const e=m("field_mapping"),i=q(),E=te(()=>i.editParamsDialogVisible),D=te(()=>i.bpmnInstance),g=m([]),b=m(),y=m(),T=m(),t=m([]),o=m([]),u=m([]);function h(k="field_mapping"){var x;let _=(x=g.value.find(({name:S})=>S===k))==null?void 0:x.value;if(A(_))return[];try{_=JSON.parse(_)}catch{_={},ne.warning(`${k} \u53C2\u6570\u89E3\u6790\u5931\u8D25`)}return Reflect.ownKeys(_).map(S=>({name:S,value:_[S]}))}async function V(){var l,r;const _=D.value.get("elementRegistry").getAll()[0].children.find(({id:f})=>f===i.nodeId),x=ee(_),S=le(x)[0],M=(l=S.values.find(({name:f})=>f==="task-component"))==null?void 0:l.value,I=(r=S.values.find(({name:f})=>f==="task-service"))==null?void 0:r.value,O=await ge({taskComponent:M,taskService:I});return(O==null?void 0:O.nodeParamConfigs[0])||[]}async function n(){const _=D.value.get("elementRegistry").getAll()[0].children.find(({id:l})=>l===i.nodeId),x=ee(_),S=le(x)[0];g.value=(S==null?void 0:S.values)||[];const M=h("field_mapping"),O=(await V()).filter(({targetName:l})=>M.every(({name:r})=>r!==l)).map(({targetName:l})=>({name:l,value:""}));t.value=[...M,...O],o.value=h("appoint_param"),u.value=h("conversion_param"),ke(()=>{b.value.setTableData(t.value),y.value.setTableData(o.value),T.value.setTableData(u.value)})}function p(){i.SetParamsDialogVisible(!1)}function P(k,_){const x=g.value.find(({name:M})=>M===k);if(_.length===0)return g.value;const S=_.reduce((M,I)=>({...M,[I.name]:I.value}),{});return A(x)?[...g.value,{name:k,value:JSON.stringify(S)}]:(x.value=JSON.stringify(S),g.value)}async function G(k,_){_.value=!0,g.value=P("field_mapping",b.value.getTableData()),g.value=P("appoint_param",y.value.getTableData()),g.value=P("conversion_param",T.value.getTableData());try{a("done",g.value),k(),p()}finally{_.value=!1}}function j(){const k=n();console.log("output->bb",k)}return Pe(()=>E.value,k=>{k&&j()}),c({show:j}),(k,_)=>{const x=C("el-tab-pane"),S=C("el-tabs"),M=C("yun-drawer");return R(),F(M,{modelValue:re(E),"onUpdate:modelValue":_[1]||(_[1]=I=>pe(E)?E.value=I:null),size:"X-large",title:"\u7F16\u8F91\u53C2\u6570\u6620\u5C04","confirm-button-text":"\u786E\u8BA4","cancel-button-text":"\u53D6\u6D88",onConfirm:G,onClosed:p},{default:v(()=>[d(S,{modelValue:e.value,"onUpdate:modelValue":_[0]||(_[0]=I=>e.value=I),class:"login-form-type"},{default:v(()=>[d(x,{label:"\u53C2\u6570\u6620\u5C04",name:"field_mapping"},{default:v(()=>[d(He,{ref_key:"fieldMappingTableRef",ref:b},null,512)]),_:1}),d(x,{label:"\u9884\u5236\u53C2\u6570",name:"appoint_param"},{default:v(()=>[d(Ze,{ref_key:"appointParamTableRef",ref:y},null,512)]),_:1}),d(x,{label:"\u53C2\u6570\u8F6C\u6362",name:"conversion_param"},{default:v(()=>[d(Qe,{ref_key:"conversionParamTableRef",ref:T},null,512)]),_:1})]),_:1},8,["modelValue"])]),_:1},8,["modelValue"])}}};const et=s=>(Be("data-v-4ee86cd6"),s=s(),Ne(),s),tt={class:"actions"},nt={class:"flex flex-row"},at={class:"container"},ot={class:"modeler"},st=et(()=>L("div",{id:"bpmn-canvas"},null,-1)),lt={key:0,id:"properties"},rt={__name:"editBpmn",setup(s){const c={VIEW:"VIEW",EDIT:"EDIT"},a=m(),e=m(!1),i=m(!1),E=xe(),D=c.EDIT,{id:g}=E.query,b=m(),y=m(),T=m();let t=null;const o=m({bpmnUrl:void 0,bpmnName:void 0}),u=Ie(),h=(l,r)=>(r=r||{},l=je[l]||l,l.replace(/{([^}]+)}/g,(f,w)=>r[w]||`{${w}}`)),V=()=>{const l=t.get("eventBus");["element.click","element.changed"].forEach(f=>{l.on(f,w=>{!w||w.element.type==="bpmn:Process"||console.log(w)})})},n=()=>{V()},p=()=>{console.log("\u521B\u5EFA\u6210\u529F!"),n()},P=async l=>{await t.importXML(l,r=>{r?console.error(r):p()})},G=async()=>await Ce({method:"get",timeout:12e4,url:o.value.bpmnUrl,headers:{"Content-Type":"multipart/form-data"}}),j=(l={taskComponent:"1",taskService:"2",serviceTaskType:"3"})=>{const r=t.get("elementRegistry"),f=t.get("modeling"),w=t.get("moddle"),B=q(),U=r.get(B.nodeId),W=ee(U),Z=se("bpmn:ExtensionElements",{values:[]},W,w),ae=se("camunda:Properties",{values:[]},Z,w),ye=[{name:"task-component",valueKey:"taskComponent"},{name:"task-service",valueKey:"taskService"},{name:"service-task-type",valueKey:"serviceTaskType"},{name:"field_mapping",valueKey:"fieldMapping"}].map(({name:_e,valueKey:Ee})=>se("camunda:Property",{name:_e,value:l[Ee]},ae,w));f.updateModdleProperties(U,ae,{values:ye}),f.updateModdleProperties(U,Z,{values:[ae]}),f.updateModdleProperties(U,W,{extensionElements:Z})},k=()=>{u.push({path:"/process/area"})},_=async()=>{const{versions:l}=await Ue({page:1,size:1,versionId:g});return console.log("output->getFormDataById",l),{...l[0]||{}}};Ve(async()=>{t=new he({container:"#bpmn-canvas",propertiesPanel:{parent:"#properties"},additionalModules:[Q.exports.BpmnPropertiesPanelModule,Q.exports.BpmnPropertiesProviderModule,Q.exports.CamundaPlatformPropertiesProviderModule,Q.exports.CloudElementTemplatesPropertiesProviderModule,{translate:["value",h]},Xe],moddleExtensions:{camunda:we}});const l=q();l.SetBpmnInstance(t),l.SetParamsDialogVisible(!1),l.SetSelectDialogVisible(!1),e.value=!0;try{const r=await _();if(o.value={...o.value,...r},A(o.value.bpmnUrl))ce(t);else{const{data:f}=await G();P(f)}}catch(r){ce(t),ne.error(r)}finally{e.value=!1}});async function x(l="bpmn"){try{if(!t)return this.$message.error("\u6D41\u7A0B\u56FE\u5F15\u64CE\u521D\u59CB\u5316\u5931\u8D25");const r=t;if(l==="xml"||l==="bpmn"){const{err:f,xml:w}=await r.saveXML();f&&console.error(`[Process Designer Warn ]: ${f.message||f}`);const B=new Blob([w],{type:"bpmn20-xml"}),U=new File([B],`${new Date().getTime()}.bpmn`,{type:B.type}),W=new FormData;return W.append("file",U),W.append("fileExtension","bpmn"),i.value=!0,(await Ae(W)).patch}}catch(r){console.error(`[Process Designer Warn ]: ${r.message||r}`)}finally{i.value=!1}}async function S(l="bpmn"){try{if(!t)return this.$message.error("\u6D41\u7A0B\u56FE\u5F15\u64CE\u521D\u59CB\u5316\u5931\u8D25");const r=t;if(l==="xml"||l==="bpmn"){const{err:f,xml:w}=await r.saveXML();f&&console.error(`[Process Designer Warn ]: ${f.message||f}`);const{href:B,filename:U}=ue(l.toUpperCase(),new Date().getTime(),w);de(B,U)}else{const{err:f,svg:w}=await r.saveSVG();if(f)return console.error(f);const{href:B,filename:U}=ue("SVG",new Date().getTime(),w);de(B,U)}}catch(r){console.error(`[Process Designer Warn ]: ${r.message||r}`)}}const M=async()=>{var w;const r=(w=t.get("elementRegistry").getAll().find(B=>B.type==="bpmn:StartEvent"))==null?void 0:w.id,f=await x();T.value.show({...o.value,startEventId:r,bpmnUrl:f})},I=()=>{var l;(l=a.value)==null||l.click()},O=()=>{try{if(!t)return this.$message.error("\u6D41\u7A0B\u56FE\u5F15\u64CE\u521D\u59CB\u5316\u5931\u8D25");const l=t;if(a.value&&a.value.files){const r=a.value.files[0],f=new FileReader;f.readAsText(r),f.onload=function(){const w=this.result;l.importXML(w)}}}catch(l){console.error(`[Process Designer Warn ]: ${l.message||l}`)}};return(l,r)=>{const f=C("el-button"),w=Re("loading");return R(),J(z,null,[L("div",tt,[L("div",null," name: "+ve(o.value.functionName),1),L("div",nt,[d(f,{onClick:r[0]||(r[0]=B=>I())},{default:v(()=>[$(" \u52A0\u8F7D\u672C\u5730 bpmn ")]),_:1}),L("input",{ref_key:"importRef",ref:a,type:"file",style:{display:"none"},accept:".xml,.bpmn",onChange:O},null,544),d(f,{onClick:r[1]||(r[1]=B=>S())},{default:v(()=>[$(" \u4E0B\u8F7D bpmn \u81F3\u672C\u5730 ")]),_:1}),L("div",null,[d(f,{loading:i.value,onClick:r[2]||(r[2]=B=>M())},{default:v(()=>[$(" \u63D0\u4EA4\u4FDD\u5B58 ")]),_:1},8,["loading"])])])]),Me((R(),J("div",at,[L("div",ot,[st,re(D)===c.EDIT?(R(),J("div",lt)):X("v-if",!0),X(" \u4FA7\u8FB9\u680F\u533A\u57DF ")])])),[[w,e.value]]),d(We,{ref_key:"selectDialogRef",ref:b,onSuccess:j},null,512),d(Ye,{ref_key:"selectParamsDrawerRef",ref:y,onDone:l.handleUpdateElementParams},null,8,["onDone"]),d(Je,{ref_key:"saveDialogRef",ref:T,onSuccess:k},null,512)],64)}}};var yt=fe(rt,[["__scopeId","data-v-4ee86cd6"]]);export{yt as default};