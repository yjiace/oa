!function(){"use strict";var n,t,e,r=tinymce.util.Tools.resolve("tinymce.PluginManager"),o=tinymce.util.Tools.resolve("tinymce.util.Tools"),u=function(n,t,e){var r="UL"===t?"InsertUnorderedList":"InsertOrderedList";n.execCommand(r,!1,!1===e?null:{"list-style-type":e})},i=function(){},l=function(n){return function(){return n}},c=l(!1),s=l(!0),f=function(){return a},a=(n=function(n){return n.isNone()},{fold:function(n,t){return n()},is:c,isSome:c,isNone:s,getOr:e=function(n){return n},getOrThunk:t=function(n){return n()},getOrDie:function(n){throw new Error(n||"error: getOrDie called on none.")},getOrNull:l(null),getOrUndefined:l(void 0),or:e,orThunk:t,map:f,each:i,bind:f,exists:c,forall:s,filter:f,equals:n,equals_:n,toArray:function(){return[]},toString:l("none()")}),d=function(n){var t=l(n),e=function(){return o},r=function(t){return t(n)},o={fold:function(t,e){return e(n)},is:function(t){return n===t},isSome:s,isNone:c,getOr:t,getOrThunk:t,getOrDie:t,getOrNull:t,getOrUndefined:t,or:e,orThunk:e,map:function(t){return d(t(n))},each:function(t){t(n)},bind:r,exists:r,forall:r,filter:function(t){return t(n)?o:a},toArray:function(){return[n]},toString:function(){return"some("+n+")"},equals:function(t){return t.is(n)},equals_:function(t,e){return t.fold(c,(function(t){return e(n,t)}))}};return o},g=function(n){return null==n?a:d(n)},m=function(n){return n&&/^(TH|TD)$/.test(n.nodeName)},p=function(n,t,e){var r=function(n,t){for(var e=0;e<n.length;e++)if(t(n[e]))return e;return-1}(t.parents,m),u=-1!==r?t.parents.slice(0,r):t.parents,i=o.grep(u,function(n){return function(t){return t&&/^(OL|UL|DL)$/.test(t.nodeName)&&function(n,t){return n.$.contains(n.getBody(),t)}(n,t)}}(n));return i.length>0&&i[0].nodeName===e},y=function(n,t,e,r,i,l){n.ui.registry.addSplitButton(t,{tooltip:e,icon:"OL"===i?"ordered-list":"unordered-list",presets:"listpreview",columns:3,fetch:function(n){n(o.map(l,(function(n){return{type:"choiceitem",value:"default"===n?"":n,icon:"list-"+("OL"===i?"num":"bull")+"-"+("disc"===n||"decimal"===n?"default":n),text:function(n){return n.replace(/\-/g," ").replace(/\b\w/g,(function(n){return n.toUpperCase()}))}(n)}})))},onAction:function(){return n.execCommand(r)},onItemAction:function(t,e){u(n,i,e)},select:function(t){return function(n){var t=n.dom.getParent(n.selection.getNode(),"ol,ul"),e=n.dom.getStyle(t,"listStyleType");return g(e)}(n).map((function(n){return t===n})).getOr(!1)},onSetup:function(t){var e=function(e){t.setActive(p(n,e,i))};return n.on("NodeChange",e),function(){return n.off("NodeChange",e)}}})},v=function(n,t,e,r,o,u){u.length>1?y(n,t,e,r,o,u):function(n,t,e,r,o,u){n.ui.registry.addToggleButton(t,{active:!1,tooltip:e,icon:"OL"===o?"ordered-list":"unordered-list",onSetup:function(t){var e=function(e){t.setActive(p(n,e,o))};return n.on("NodeChange",e),function(){return n.off("NodeChange",e)}},onAction:function(){return n.execCommand(r)}})}(n,t,e,r,o)};r.add("advlist",(function(n){(function(n,t){return-1!==o.inArray(n.getParam("plugins","","string").split(/[ ,]/),t)})(n,"lists")&&(function(n){v(n,"numlist","Numbered list","InsertOrderedList","OL",function(n){var t=n.getParam("advlist_number_styles","default,lower-alpha,lower-greek,lower-roman,upper-alpha,upper-roman");return t?t.split(/[ ,]/):[]}(n)),v(n,"bullist","Bullet list","InsertUnorderedList","UL",function(n){var t=n.getParam("advlist_bullet_styles","default,circle,square");return t?t.split(/[ ,]/):[]}(n))}(n),function(n){n.addCommand("ApplyUnorderedListStyle",(function(t,e){u(n,"UL",e["list-style-type"])})),n.addCommand("ApplyOrderedListStyle",(function(t,e){u(n,"OL",e["list-style-type"])}))}(n))}))}();