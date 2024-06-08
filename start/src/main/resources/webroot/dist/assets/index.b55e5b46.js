import{q as P,u as F,a as z}from"./log.c2d353d2.js";import{u as D}from"./index.6a0f63a4.js";import{a0 as q,b as R,r as d,g as s,o as g,v as S,w as h,a as B,x as H,e as I,i as c,c as V,l as N,k as T,u as k,F as U,$ as C}from"./index.14aff2f2.js";import"./index.82e1f583.js";const E=(i,p)=>i==null||i==null||i==""?"":q(i).format(p||"YYYY-MM-DD HH:mm:ss");const $=["textContent"],M={__name:"exceptionDialog",props:{title:{type:String,default:"\u5F02\u5E38\u4FE1\u606F"}},setup(i,{expose:p}){const l=d(!1),u=d(""),f=o=>{u.value=o.exception+o.exception+o.exception,l.value=!0},m=o=>{o()};return p({show:f}),(o,b)=>{const v=s("yun-dialog");return g(),S(v,{modelValue:l.value,"onUpdate:modelValue":b[0]||(b[0]=x=>l.value=x),title:i.title,size:"large","confirm-button-handler":m,"confirm-button-text":"\u5173\u95ED","show-cancel-button":!1},{default:h(()=>[B("div",{class:"info",textContent:H(u.value)},null,8,$)]),_:1},8,["modelValue","title"])}}};var Y=R(M,[["__scopeId","data-v-ebad2332"]]),L={__name:"detail",setup(i,{expose:p}){const l=d(!1),u=d(""),f=d(),m=d(),o={page:1,size:10,total:0};async function b({pagination:t}){const{page:e,size:a}=t,r=await P({requestId:u.value,page:e,size:a});return{...r,records:r.elementExampleList.map(_=>{const{oilCard:n,...w}=_;return{...w,...n}})}}const v=async t=>{u.value=t.id,l.value=!0},x=I(()=>[{label:"\u529F\u80FD\u540D\u79F0",prop:"functionName"},{prop:"flowElementName",label:"\u8282\u70B9\u540D\u79F0"},{prop:"status",label:"\u6267\u884C\u72B6\u6001",render:({row:t})=>t.status==="SUSS"?"\u6210\u529F":"\u5931\u8D25"},{label:"\u5165\u53C2",prop:"requestParam",width:320,render:({row:t})=>c(s("json-viewer"),{value:t.requestParam,copyable:!0,sort:!0,"expand-depth":0},null)},{label:"\u51FA\u53C2",prop:"returnParam",width:320,render:({row:t})=>c(s("json-viewer"),{value:t.returnParam,copyable:!0,sort:!0,"expand-depth":0},null)},{label:"\u8017\u65F6",prop:"takeTime",render:({row:t})=>`${t.takeTime||"0"}ms`},{label:"\u5F02\u5E38\u4FE1\u606F",prop:"exception"}]);function y(t){m.value.show(t)}return p({show:v}),(t,e)=>{const a=s("el-button"),r=s("yun-pro-table"),_=s("yun-drawer");return g(),V(U,null,[c(_,{modelValue:l.value,"onUpdate:modelValue":e[1]||(e[1]=n=>l.value=n),title:"\u8BE6\u60C5",size:"X-large"},{default:h(()=>[l.value?(g(),S(r,{key:0,ref_key:"tableRef",ref:f,pagination:o,"onUpdate:pagination":e[0]||(e[0]=n=>o=n),"table-columns":k(x),"remote-method":b,"table-props":{stripe:!1}},{t_exception:h(({row:n})=>[n.status!=="SUSS"?(g(),S(a,{key:0,type:"action",onClick:w=>y(n)},{default:h(()=>[N(" \u67E5\u770B\u8BE6\u7EC6 ")]),_:2},1032,["onClick"])):T("v-if",!0)]),_:1},8,["table-columns"])):T("v-if",!0)]),_:1},8,["modelValue"]),c(Y,{ref_key:"exceptionDialogRef",ref:m},null,512)],64)}}},j={__name:"index",setup(i){const p=d(null),l=e=>{p.value.show(e)},u=[{label:"\u5E8F\u53F7",type:"index"},{label:"\u529F\u80FD\u540D\u79F0",prop:"functionName"},{label:"\u8BF7\u6C42\u7F16\u53F7",prop:"id"},{label:"\u6267\u884C\u72B6\u6001",prop:"status",render:({row:e})=>e.status==="SUSS"?"\u6210\u529F":"\u5931\u8D25"},{label:"\u6267\u884C\u5F00\u59CB\u65F6\u95F4",prop:"startTime",render:({row:e})=>E(e.startTime),width:180},{label:"\u6267\u884C\u7ED3\u675F\u65F6\u95F4",prop:"endTime",render:({row:e})=>E(e.endTime)},{label:"\u8017\u65F6",prop:"takeTime",render:({row:e})=>`${e.takeTime||"0"}ms`},{label:"\u64CD\u4F5C",prop:"action",render:({row:e})=>c(s("el-button"),{type:"action",onClick:()=>l(e)},{default:()=>[N("\u67E5\u770B\u8BE6\u60C5")]})}],f=[D("functionName","\u529F\u80FD\u540D\u79F0"),D("businessParam","\u4E1A\u52A1\u7F16\u53F7"),D("id","\u8BF7\u6C42\u7F16\u53F7")],m=d({}),{pagination:o,remoteMethod:b,tableProps:v,proTableRef:x,filterTableData:y}=F({apiFn:z,extParams:{},extQuerys:{sort:"createTime__DESC"},optimize:!0,async paramsHandler(e){return{...JSON.parse(JSON.stringify(e))}},responseHandler(e){const{flowExampleList:a,total:r}=e;return{records:a,total:r}},plugins:{config:{columns:u,searchFields:f},list:["SEARCH_PLUS"]}}),t=({searchData:e,filterData:a,pagination:r})=>{const _={...e,page:r.page,size:r.pageSize};return b({searchData:_,filterData:a,pagination:r})};return(e,a)=>{const r=s("yun-pro-table"),_=s("el-card");return g(),V(U,null,[c(_,{"body-style":{padding:"0 24px 24px 24px"},shadow:"never"},{default:h(()=>[c(r,{ref_key:"proTableRef",ref:x,pagination:k(o),"onUpdate:pagination":a[0]||(a[0]=n=>C(o)?o.value=n:null),"filter-data":k(y),"onUpdate:filter-data":a[1]||(a[1]=n=>C(y)?y.value=n:null),searchData:m.value,"onUpdate:searchData":a[2]||(a[2]=n=>m.value=n),"search-fields":f,"table-columns":u,"remote-method":t,"table-props":k(v)},null,8,["pagination","filter-data","searchData","table-props"])]),_:1}),c(L,{ref_key:"detailRef",ref:p},null,512)],64)}}};var X=R(j,[["__scopeId","data-v-6edcf0eb"]]);export{X as default};
