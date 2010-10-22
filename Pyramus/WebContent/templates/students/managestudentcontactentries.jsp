<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head> 
    <title><fmt:message key="students.manageStudentContactEntries.pageTitle"></fmt:message></title>
    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/datefield_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/dialog_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/hovermenu_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>

    <!-- Used to render memo values with line breaks; for some reason this is the only approach that works -->
    <% pageContext.setAttribute("newLineChar", "\n"); %>

    <script type="text/javascript">
      function setupTabRelatedActions(abstractStudentId, studentId) {
        var basicTabRelatedActionsHoverMenu = new IxHoverMenu($('basicTabRelatedActionsHoverMenuContainer.' + studentId), {
          text: '<fmt:message key="students.manageStudentContactEntries.basicTabRelatedActionsLabel"/>'
        });
    
        basicTabRelatedActionsHoverMenu.addItem(new IxHoverMenuLinkItem({
          iconURL: GLOBAL_contextPath + '/gfx/eye.png',
          text: '<fmt:message key="students.manageStudentContactEntries.basicTabRelatedActionsViewStudentLabel"/>',
          link: GLOBAL_contextPath + '/students/viewstudent.page?abstractStudent=' + abstractStudentId  
        }));

        basicTabRelatedActionsHoverMenu.addItem(new IxHoverMenuLinkItem({
          iconURL: GLOBAL_contextPath + '/gfx/accessories-text-editor.png',
          text: '<fmt:message key="students.manageStudentContactEntries.basicTabRelatedActionsEditStudentLabel"/>',
          link: GLOBAL_contextPath + '/students/editstudent.page?abstractStudent=' + abstractStudentId  
        }));
      }

      function onLoad(event) {
        <c:forEach var="student" items="${students}">
          // Setup basics
          setupTabRelatedActions(${abstractStudent.id}, ${student.id}); 
        </c:forEach>
        
        var tabControl2 = new IxProtoTabs($('studentTabs'));

        <c:forEach var="student" items="${students}">
          resetEntryForm(${student.id});
        </c:forEach>

        <c:if test="${!empty param.activeTab}">
          tabControl.setActiveTab("${param.activeTab}");  
        </c:if>
      }

      function resetEntryForm(studentId) {
        var entryForm = $("newContactEntryForm." + studentId);
        entryForm.entryType.value = 'OTHER';
        entryForm.entryCreatorName.value = '${loggedUserName}';
        var dField = getIxDateField("entryDate." + studentId);
        if (dField != null)
          dField.setTimestamp(new Date().getTime());
        entryForm["entryText." + studentId].value = '';
        CKEDITOR.instances["entryText." + studentId].setData('');
        entryForm.entryId.value = '-1';
        entryForm.submitContactLogEntryButton.value = "<fmt:message key="students.manageStudentContactEntries.newContactLogEntryBtn"/>";
      }

      function resetEntryForm2(event, studentId) {
        Event.stop(event);
        resetEntryForm(studentId);
      }

      function editEntry(contactEntryId, studentId) {
        JSONRequest.request("students/getcontactentry.json", {
          parameters: {
            entryId: contactEntryId
          },
          onSuccess: function (jsonResponse) {
            var results = jsonResponse.results;
            var entryId = results.id;
            var studentId = results.studentId;
            var entryDate = new Date(results.timestamp);
            var creatorName = results.creatorName;
            var entryType = results.type;
            var entryText = results.text;

            var entryForm = $("newContactEntryForm." + studentId);
            entryForm.entryType.value = entryType;
            entryForm.entryCreatorName.value = creatorName;
            var dField = getIxDateField("entryDate." + studentId);
            if (dField != null) {
              if (entryDate != null)
                dField.setTimestamp(entryDate.getTime());
              else
                dField.setTimestamp(new Date().getTime());
            }
            entryForm["entryText." + studentId].value = entryText;
            CKEDITOR.instances["entryText." + studentId].setData(entryText);
            entryForm.entryId.value = entryId;
            entryForm.submitContactLogEntryButton.value = "<fmt:message key="students.manageStudentContactEntries.editContactLogEntryBtn"/>";
          } 
        });
      }

      function saveEvent(event, studentId) {
        var entryForm = $("newContactEntryForm." + studentId);
        var entryId = entryForm.entryId.value;
        
        if (entryId == -1)
          newContactEntryFormSubmit(event, studentId);
        else
          modifyContactEntryFormSubmit(event, studentId);        
      }

      function getEntryTypeName(entryType) {
        var entryTypeName = "???";
        
        if (entryType == 'OTHER')
          entryTypeName = '<fmt:message key="students.manageStudentContactEntries.contactEntry.types.other"/>';
        else
        if (entryType == 'LETTER')
          entryTypeName = '<fmt:message key="students.manageStudentContactEntries.contactEntry.types.letter"/>';
        else
        if (entryType == 'EMAIL')
          entryTypeName = '<fmt:message key="students.manageStudentContactEntries.contactEntry.types.email"/>';
        else
        if (entryType == 'PHONE')
          entryTypeName = '<fmt:message key="students.manageStudentContactEntries.contactEntry.types.phone"/>';
        else
        if (entryType == 'CHATLOG')
          entryTypeName = '<fmt:message key="students.manageStudentContactEntries.contactEntry.types.chatlog"/>';
        else
        if (entryType == 'SKYPE')
          entryTypeName = '<fmt:message key="students.manageStudentContactEntries.contactEntry.types.skype"/>';
        else
        if (entryType == 'FACE2FACE')
          entryTypeName = '<fmt:message key="students.manageStudentContactEntries.contactEntry.types.face2face"/>';

        return entryTypeName;
      }

      function archiveEntry(entryId, studentId) {
        var entryShort = $("entry." + entryId + ".text").textContent;
        if (entryShort.length > 20)
          entryShort = entryShort.substring(0, 19) + "...";
        var url = GLOBAL_contextPath + "/simpledialog.page?localeId=students.manageStudentContactEntries.archiveContactEntryConfirmDialogContent&localeParams=" + encodeURIComponent(entryShort);
        var dialog = new IxDialog({
          id : 'confirmRemoval',
          contentURL : url,
          centered : true,
          showOk : true,  
          showCancel : true,
          autoEvaluateSize: true,
          title : '<fmt:message key="students.manageStudentContactEntries.archiveContactEntryConfirmDialogTitle"/>',
          okLabel : '<fmt:message key="students.manageStudentContactEntries.archiveContactEntryConfirmDialogOkLabel"/>',
          cancelLabel : '<fmt:message key="students.manageStudentContactEntries.archiveContactEntryConfirmDialogCancelLabel"/>'
        });
      
        dialog.addDialogListener(function(event) {
          switch (event.name) {
            case 'okClick':
              JSONRequest.request("students/archivecontactentry.json", {
                parameters: {
                  entryId: entryId
                },
                onSuccess: function (jsonResponse) {
                  var entryItem = $('studentContactEntryItem.' + entryId);

                  if (entryItem != null)
                    entryItem.remove();                  
                }
              });   
            break;
          }
        });

        dialog.open();
      }
      
      function addEntryRow(studentId, entryId, entryDate, entryType, entryCreatorName, entryText) {
        var listDiv = $('studentContactEntryList.' + studentId);
        var date = new Date(entryDate);
        var dateStr = date.getDate() + "." + (date.getMonth() + 1) + "." + date.getFullYear();

        var entryTypeName = getEntryTypeName(entryType);

        var newEntryDiv = listDiv.appendChild(Builder.node("div", {id: "studentContactEntryItem." + entryId, class: "studentContactEntryItem"}));
        var newEntryCaptionDiv = newEntryDiv.appendChild(Builder.node("div", {id: "entry." + entryId + ".caption", class: "studentContactEntryCaption"}));
        
        var newEntryCaptionDateSpan = newEntryCaptionDiv.appendChild(Builder.node("span", {class: "studentContactEntryDate"}, [dateStr])); 
        var newEntryCaptionTypeSpan = newEntryCaptionDiv.appendChild(Builder.node("span", {class: "studentContactEntryType"}, [entryTypeName])); 
        var newEntryCaptionTypeSpan = newEntryCaptionDiv.appendChild(Builder.node("span", {class: "studentContactEntryCreator"}, [entryCreatorName])); 
        
        var buttonsDiv = newEntryDiv.appendChild(Builder.node("div", {class: "studentContactEntryButtons"}));
        buttonsDiv.appendChild(Builder.node("img", {id: "entry." + entryId + ".editbtn", class: "studentContactEntryEditButton", 
          src: "${pageContext.request.contextPath}/gfx/accessories-text-editor.png", 
          onClick: "editEntry(" + entryId + ", " + studentId + ")"}, []));
        buttonsDiv.appendChild(Builder.node("img", {id: "entry." + entryId + ".archivebnt", class: "studentContactEntryArchiveButton", 
          src: "${pageContext.request.contextPath}/gfx/list-remove.png", 
          onClick: "archiveEntry(" + entryId + ", " + studentId + ")"}, []));

        var node = Builder.node("div", {id: "entry." + entryId + ".text"}, []);
        node.innerHTML = entryText;
        newEntryDiv.appendChild(node);
      }
      
      /**
       * 
       *
       * @param event The submit event
       */
      function newContactEntryFormSubmit(event, studentId) {
        Event.stop(event);

        var entryForm = Event.element(event);
        JSONRequest.request("students/createnewcontactentry.json", {
          parameters: {
            entryType: entryForm.entryType.value,
            entryCreatorName: entryForm.entryCreatorName.value,
            entryDate: entryForm["entryDate." + studentId].value,
            entryText: CKEDITOR.instances["entryText." + studentId].getData(),
            studentId: entryForm.studentId.value
          },
          onSuccess: function (jsonResponse) {
            var studentId = jsonResponse.results.studentId;
            var results = jsonResponse.results;
            var entryDate = new Date(results.timestamp);
            var entryId = results.id;

            addEntryRow(studentId, entryId, entryDate, results.type, results.creatorName, results.text);
                                    
            resetEntryForm(studentId);
          } 
        });
      }
      
      function modifyContactEntryFormSubmit(event, studentId) {
        Event.stop(event);

        var entryForm = Event.element(event);
        JSONRequest.request("students/editcontactentry.json", {
          parameters: {
            entryType: entryForm.entryType.value,
            entryCreatorName: entryForm.entryCreatorName.value,
            entryId: entryForm.entryId.value,
            entryDate: entryForm["entryDate." + studentId].value,
            entryText: CKEDITOR.instances["entryText." + studentId].getData()
          },
          onSuccess: function (jsonResponse) {
            var results = jsonResponse.results;
            var entryId = results.id;
            var studentId = results.studentId;
            var entryDate = new Date(results.timestamp);
            var creatorName = results.creatorName;
            var entryType = results.type;
            var entryText = results.text;

            var date = new Date(entryDate);
            var dateStr = date.getDate() + "." + (date.getMonth() + 1) + "." + date.getFullYear();
            var entryTypeName = getEntryTypeName(entryType);
            var entryCaption = dateStr + ' &lt;' + entryTypeName + '&gt; ' + creatorName;

            $("entry." + entryId + ".caption").innerHTML = entryCaption;
            $("entry." + entryId + ".text").innerHTML = entryText;
                                                
            resetEntryForm(studentId);
          } 
        });
      }        
    </script>
  </head>

  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="students.manageStudentContactEntries.pageTitle" /> (${abstractStudent.latestStudent.fullName})</h1>
  
    <div id="viewStudentViewContainer"> 
      <div class="genericFormContainer"> 
        <div class="tabLabelsContainer" id="studentTabs">
          <c:forEach var="student" items="${students}">
            <a class="tabLabel" href="#student.${student.id}">
              <c:choose>
                <c:when test="${student.studyProgramme == null}">
                   <fmt:message key="students.manageStudentContactEntries.noStudyProgrammeTabLabel"/>
                </c:when>
                <c:otherwise>
                  ${student.studyProgramme.name}
                </c:otherwise>
              </c:choose>
              <c:if test="${student.studyEndDate ne null}">*</c:if>
            </a>
          </c:forEach>
        </div>
    
        <c:forEach var="student" items="${students}">
          <div id="student.${student.id}" class="tabContent">
            <div id="basicTabRelatedActionsHoverMenuContainer.${student.id}" class="tabRelatedActionsContainer"></div>
  
            <div id="viewStudentViewContainer"> 
              <div class="genericFormContainer"> 
                <form method="post" id="newContactEntryForm.${student.id}" onsubmit="saveEvent(event, ${student.id});">
                  <input type="hidden" name="studentId" value="${student.id}"/>
                  <input type="hidden" name="entryId" value="-1"/>
      
                  <div id="studentContactEntryList.${student.id}" class="studentContactEntryWrapper">
                    <script type="text/javascript">
                    <c:forEach var="contactEntry" items="${contactEntries[student.id]}">
                      addEntryRow(
                          ${student.id}, 
                          ${contactEntry.id}, 
                          ${contactEntry.entryDate.time}, 
                          '${contactEntry.type}', 
                          '${fn:replace(contactEntry.creatorName, "'", "\\'")}', 
                          '${fn:replace(fn:replace(contactEntry.text, newLineChar, ""), "'", "\\'")}'
                      );
                    </c:forEach>
                    </script>
                  </div>
      
                  <div class="genericFormSection">                            
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.manageStudentContactEntries.contactEntry.typeTitle"/>
                      <jsp:param name="helpLocale" value="students.manageStudentContactEntries.contactEntry.typeHelp"/>
                    </jsp:include> 
                    <select name="entryType">
                      <option value="OTHER"><fmt:message key="students.manageStudentContactEntries.contactEntry.types.other"/></option>
                      <option value="LETTER"><fmt:message key="students.manageStudentContactEntries.contactEntry.types.letter"/></option>
                      <option value="EMAIL"><fmt:message key="students.manageStudentContactEntries.contactEntry.types.email"/></option>
                      <option value="PHONE"><fmt:message key="students.manageStudentContactEntries.contactEntry.types.phone"/></option>
                      <option value="CHATLOG"><fmt:message key="students.manageStudentContactEntries.contactEntry.types.chatlog"/></option>
                      <option value="SKYPE"><fmt:message key="students.manageStudentContactEntries.contactEntry.types.skype"/></option>
                      <option value="FACE2FACE"><fmt:message key="students.manageStudentContactEntries.contactEntry.types.face2face"/></option>
                    </select>
                  </div>            
                  <div class="genericFormSection">                            
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.manageStudentContactEntries.contactEntry.fromTitle"/>
                      <jsp:param name="helpLocale" value="students.manageStudentContactEntries.contactEntry.fromHelp"/>
                    </jsp:include> 
                    <input type="text" name="entryCreatorName"/>
                  </div> 
                  <div class="genericFormSection">                            
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.manageStudentContactEntries.contactEntry.dateTitle"/>
                      <jsp:param name="helpLocale" value="students.manageStudentContactEntries.contactEntry.dateHelp"/>
                    </jsp:include> 
                    <input type="text" name="entryDate.${student.id}" ix:datefieldid="entryDate.${student.id}" ix:datefield="true"/>
                  </div>
                  <div class="genericFormSection">                            
                    <jsp:include page="/templates/generic/fragments/formtitle.jsp">
                      <jsp:param name="titleLocale" value="students.manageStudentContactEntries.contactEntry.textTitle"/>
                      <jsp:param name="helpLocale" value="students.manageStudentContactEntries.contactEntry.textHelp"/>
                    </jsp:include> 
                    <textarea name="entryText.${student.id}" cols="60" rows="6" ix:cktoolbar="studentContactEntryText" ix:ckeditor="true"></textarea>
                  </div>            
                  <div>
                    <input type="submit" name="submitContactLogEntryButton" value="<fmt:message key="students.manageStudentContactEntries.newContactLogEntryBtn"/>">
                    <input type="button" name="clearContactLogEntryButton" value="<fmt:message key="students.manageStudentContactEntries.resetContactLogEntryFormBtn"/>" onClick="resetEntryForm2(event, ${student.id});">
                  </div>            
                </form>
              </div>
            </div>  
          </div>
        </c:forEach>
      </div>
    </div>  

    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>