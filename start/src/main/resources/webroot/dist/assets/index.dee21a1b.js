import{t as g,e as p,g as u,o as d,v as f,w as F,a as y,x as h,u as o,i as e,l as i,F as _,s as x,r as E,c as D,G as k,M as C,b as B}from"./index.14aff2f2.js";const M={__name:"FilterItemLabel",props:{label:{type:String,default:""}},setup(t){const r=t,{label:a}=g(r),n=8,l=p(()=>a.value.length>n),c=p(()=>l.value?`${a.value.slice(0,7)}...`:a.value);return(m,v)=>{const s=u("el-tooltip");return d(),f(s,{content:o(a),placement:"bottom",disabled:!o(l)},{default:F(()=>[y("span",null,h(o(c)),1)]),_:1},8,["content","disabled"])}}},Y=[{prop:"value1",label:"\u6587\u672C\u8F93\u5165\u6846",type:"input",attrs:{placeholder:"\u8BF7\u8F93\u5165\u540D\u79F0"},rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u4FE1\u606F",trigger:"input"}]},{prop:"value2",label:"\u4E0B\u62C9\u5DF2\u9009",tip:"\u8FD9\u662F\u8F85\u52A9\u63D0\u793A",type:"select",enums:[{label:"\u9009\u9879\u4E00",value:"1"},{label:"\u9009\u9879\u4E8C",value:"2"},{label:"\u9009\u9879\u4E09",value:"3"},{label:"\u9009\u9879\u56DB",value:"4"},{label:"\u9009\u9879\u4E94",value:"5"}]},{prop:"value3",label:"\u5355\u9009\u9009\u62E9",type:"radio",enums:[{label:"\u6309\u90E8\u95E8\u9009\u62E9",value:"1"},{label:"\u6309\u516C\u53F8\u9009\u62E9",value:"2"}]},{prop:"value4",renderLabel:()=>e(M,{label:"\u6700\u591A\u516B\u4E2A\u5B57\u8D85\u8FC7\u663E\u793A"},null),type:"select",enums:[{label:"\u6807\u7B7E\u4E00",value:"1"},{label:"\u6807\u7B7E\u4E8C",value:"2"},{label:"\u6807\u7B7E\u4E09",value:"3"}],attrs:{multiple:!0,collapseTags:!0},rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u4FE1\u606F",trigger:"input"}]},{prop:"value5",label:"\u65E5\u671F\u9009\u62E9",type:"date-picker",attrs:{placeholder:"\u8BF7\u9009\u62E9",valueFormat:"YYYY-MM-DD"},rules:[{required:!0,message:"\u8BF7\u9009\u62E9\u65E5\u671F",trigger:"change"}]},{prop:"value6",label:"\u65F6\u95F4\u9009\u62E9",type:"date-picker",attrs:{type:"daterange",rangeSeparator:"\u81F3",startPlaceholder:"\u5F00\u59CB\u65E5\u671F",endPlaceholder:"\u7ED3\u675F\u65E5\u671F",valueFormat:"YYYY-MM-DD"},rules:[{required:!0,message:"\u8BF7\u9009\u62E9\u65F6\u95F4",trigger:"change"}]},{prop:"value7",label:"\u591A\u9009\u6761\u4EF6",type:"checkbox",enums:[{label:"\u5DF2\u9009\u4E2D\u9879",value:"1"},{label:"\u672A\u9009\u4E2D\u9879",value:"2"}]},{prop:"value8",label:"\u5F00\u5173",type:"switch",attrs:{activeText:"\u5F00",inactiveText:"\u5173",inlinePrompt:!0}},{prop:"value9",label:"\u6587\u672C\u57DF",render:t=>e(_,null,[e(u("el-input"),{modelValue:t.value9,"onUpdate:modelValue":r=>t.value9=r,rows:2,type:"textarea"},null),e("p",{class:"form-tips"},[i("\u8FD9\u91CC\u663E\u793A\u989D\u5916\u7684\u5E2E\u52A9\u8BF4\u660E\u6216\u8F85\u52A9\u4FE1\u606F")])]),rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u4FE1\u606F",trigger:"input"}]}],L=()=>({value1:"",value2:"1",value3:"2",value4:["1","2","3"],value5:"",value6:[],value7:["1"],value8:!0,value9:""});function T(){return{columns:Y,initForm:L}}const q={class:"base-form"},N={name:"BaseForm"};var S=Object.assign(N,{setup(t){const{columns:r,initForm:a}=T(),n=x(a()),l=E(),c=()=>{l.value.elForm.validate(s=>{s&&(k.success("\u63D0\u4EA4\u6210\u529F"),C({title:"\u7ED3\u679C",message:JSON.stringify(l.value.getForm(!0))}))})},m=()=>{l.value.elForm.resetFields()},v=p(()=>[...r,{render:()=>e(_,null,[e(u("el-button"),{type:"primary",onClick:c},{default:()=>[i("\u4FDD\u5B58\u5E76\u9884\u89C8")]}),e(u("el-button"),{onClick:m},{default:()=>[i("\u91CD\u7F6E")]})])}]);return(s,V)=>{const b=u("yun-pro-form");return d(),D("div",q,[e(b,{ref_key:"formRef",ref:l,"custom-class":"base-form__main",form:n,columns:o(v),"form-props":{labelWidth:135},config:{colProps:{span:24}}},null,8,["form","columns"])])}}});var I=B(S,[["__scopeId","data-v-f8026b74"]]);export{I as default};
