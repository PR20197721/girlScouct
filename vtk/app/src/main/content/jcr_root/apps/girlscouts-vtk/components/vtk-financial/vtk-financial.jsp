<%@include file="/libs/foundation/global.jsp" %>
##### VTK Financial Tab Settings #####
<div>
  <p>
    Financial Summary: <%= properties.get("finsum1", "") %><br>
    Review and Publish Line 1: <%= properties.get("reviewandpublish1", "") %><br>
    Review and Publish Line 2: <%= properties.get("reviewandpublish2", "") %><br>
    TL Income Expense Instruction: <%= properties.get("incomeexpenseinst", "") %><br>
    TL Message about parent view: <%= properties.get("messageabout", "") %><br>
    TL preview instructions 1: <%= properties.get("tlpreviewinst1", "") %><br>
    TL preview instructions 2: <%= properties.get("tlpreviewinst2", "") %><br>
    Notes and Questions: <%= properties.get("finsum2", "") %><br>
    Document Attachments: <%= properties.get("documentattachments", "") %><br>
    Set Up Tip Last Updated: <%= properties.get("setuptiplastupdated", "") %><br>
    Set Up Tip: <%= properties.get("setuptip", "") %><br>
  </p>
</div>
