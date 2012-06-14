var studyEndReasons = JSDATA["studyEndReasons"].evalJSON();
var topLevelReasons = JSDATA["topLevelReasons"].evalJSON();

var deletedRowIndex;

function addStudyEndReasonsTableRow() {
	var table = getIxTableById('studyEndReasonsTable');
	var rowIndex = table.addRow([ '', '', '', '', '', -1, 1 ]);
	for ( var i = 0; i < table.getColumnCount(); i++) {
		table.setCellEditable(rowIndex, i, true);
	}
	$('noStudyEndReasonsAddedMessageContainer').setStyle({
		display : 'none'
	});
	table.showCell(rowIndex, table.getNamedColumnIndex('removeButton'));
	table.hideCell(rowIndex, table.getNamedColumnIndex('deleteButton'));
}

function onLoad(event) {
	tabControl = new IxProtoTabs($('tabs'));

	var studyEndReasonsTable = new IxTable(
			$('studyEndReasonsTableContainer'),
			{
				id : "studyEndReasonsTable",
				columns : [
						{
							left : 8,
							width : 30,
							dataType : 'button',
							imgsrc : GLOBAL_contextPath
									+ '/gfx/accessories-text-editor.png',
							tooltip : getLocale().getText("settings.studyEndReasons.studyEndReasonsTableEditTooltip"),
							onclick : function(event) {
								var table = event.tableComponent;
								for ( var i = 0; i < table.getColumnCount(); i++) {
									table
											.setCellEditable(
													event.row,
													i,
													table.isCellEditable(
															event.row, i) == false);
								}
								table.setCellValue(event.row, table
										.getNamedColumnIndex('modified'), 1);
							}
						},
						{
							header : getLocale().getText("settings.studyEndReasons.studyEndReasonsTableNameHeader"),
							left : 38,
							width : 300,
							dataType : 'text',
							editable : false,
							paramName : 'name',
							required : true
						},
						{
							header : getLocale().getText("settings.studyEndReasons.parentReasonHeader"),
							left : 8 + 22 + 8 + 300 + 8,
							right : 8 + 22 + 8,
							dataType : 'select',
							editable : false,
							paramName : 'parentReasonId',
							options : topLevelReasons
						/* [
						  {text: "-", value: ''}<c:if test="${fn:length(topLevelReasons) gt 0}">,</c:if>
						  <c:forEach var="parentReason" items="${topLevelReasons}" varStatus="vs">
						    {text: "${fn:escapeXml(parentReason.name)}", value: ${parentReason.id}}
						    <c:if test="${not vs.last}">,</c:if>
						  </c:forEach>
						] */
						},
						{
							right : 8,
							width : 30,
							dataType : 'button',
							imgsrc : GLOBAL_contextPath
									+ '/gfx/edit-delete.png',
							tooltip : getLocale().getText("settings.studyEndReasons.studyEndReasonsTableDeleteTooltip"),
							onclick : function(event) {
								var table = event.tableComponent;
								var studyEndReasonId = table
										.getCellValue(
												event.row,
												table
														.getNamedColumnIndex('studyEndReasonId'));
								var studyEndReasonName = table.getCellValue(
										event.row, table
												.getNamedColumnIndex('name'));
								var url = GLOBAL_contextPath
										+ "/simpledialog.page?localeId=settings.studyEndReasons.studyEndReasonDeleteConfirmDialogContent&localeParams="
										+ encodeURIComponent(studyEndReasonName);

								deletedRowIndex = event.row;

								var dialog = new IxDialog(
										{
											id : 'confirmRemoval',
											contentURL : url,
											centered : true,
											showOk : true,
											showCancel : true,
											autoEvaluateSize : true,
											title : getLocale().getText("settings.studyEndReasons.studyEndReasonDeleteConfirmDialogTitle"),
											okLabel : getLocale().getText("settings.studyEndReasons.studyEndReasonDeleteConfirmDialogOkLabel"),
											cancelLabel : getLocale().getText("settings.studyEndReasons.studyEndReasonDeleteConfirmDialogCancelLabel")
										});

								dialog
										.addDialogListener(function(event) {
											switch (event.name) {
											case 'okClick':
												JSONRequest
														.request(
																"settings/deletestudyendreason.json",
																{
																	parameters : {
																		studyEndReason : studyEndReasonId
																	},
																	onSuccess : function(
																			jsonResponse) {
																		getIxTableById(
																				'studyEndReasonsTable')
																				.deleteRow(
																						deletedRowIndex);
																	}
																});
												break;
											}
										});

								dialog.open();
							},
							paramName : 'deleteButton'
						},
						{
							right : 8,
							width : 30,
							dataType : 'button',
							imgsrc : GLOBAL_contextPath
									+ '/gfx/list-remove.png',
							tooltip : getLocale().getText("settings.studyEndReasons.studyEndReasonsTableRemoveTooltip"),
							onclick : function(event) {
								event.tableComponent.deleteRow(event.row);
								if (event.tableComponent.getRowCount() == 0) {
									$('noStudyEndReasonsAddedMessageContainer')
											.setStyle({
												display : ''
											});
								}
							},
							paramName : 'removeButton',
							hidden : true
						}, {
							dataType : 'hidden',
							paramName : 'studyEndReasonId'
						}, {
							dataType : 'hidden',
							paramName : 'modified'
						} ]
			});

	var rows = [];
	for ( var i = 0, l = studyEndReasons.length; i < l; i++) {
		rows.push([ '', studyEndReasons[i].name, studyEndReasons[i].parentId,
				'', '', studyEndReasons[i].id, 0 ]);
	}
	/*
	<c:forEach var="studyEndReason" items="${studyEndReasons}">
	  rows.push([
	    '',
	    '${fn:escapeXml(studyEndReason.name)}',
	    '${studyEndReason.parentReason.id}',
	    '',
	    '',
	    ${studyEndReason.id},
	    0
	  ]);
	</c:forEach>
	 */
	studyEndReasonsTable.addRows(rows);

	if (studyEndReasonsTable.getRowCount() > 0) {
		$('noStudyEndReasonsAddedMessageContainer').setStyle({
			display : 'none'
		});
	}
}
