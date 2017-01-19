<%--
  ADOBE CONFIDENTIAL

  Copyright 2015 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.

--%><%@page session="false" import="com.adobe.granite.ui.components.AttrBuilder,
                                    com.adobe.granite.ui.components.Config" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    Config cfg = new Config(resource);
    AttrBuilder attr = new AttrBuilder(slingRequest, xssAPI);
    attr.addClass("taginfo");
    attr.addOther("component-path", resource.getPath());

%><div <%= attr.build() %>>
        <div class="refSpinner">
            <div>
                <div class="coral-Wait coral-Wait--large coral-Wait--center"></div>
            </div>
        </div>
        <div class="tag-info-heading">
            <div class="coral-ColumnView-preview-label tag-info-label"><%=i18n.get("NAME")%></div>
            <div class="coral-ColumnView-preview-value tag-info-name-value"></div>
        </div>

        <div class="tag-info-references-div tag-info-non-empty-references">
            <div class="endor-Badge tag-info-references-badge tag-info-non-empty-references-badge"></div>
            <a class="tag-info-references-text"><%= i18n.get("SHOW TAG REFERENCES") %></a>
        </div>

        <div class="tag-info-references-div tag-info-empty-references">
            <div class="endor-Badge tag-info-references-badge is-empty">0</div>
            <a class="coral-ColumnView-preview-value tag-info-references-text"><%= i18n.get("No References to list") %></a>            
        </div>        

        <div class="tag-info-modification">
            <div class="coral-ColumnView-preview-label tag-info-label"><%=i18n.get("MODIFIED")%></div>
            <div class="tag-info-detail-minor">
                <div class="tag-info-detail-minor-left tag-info-modified"></div>
                <div class="coral-ColumnView-preview-label tag-info-detail-minor-right tag-info-modifiedby"></div>
            </div>
        </div>

        <div class="tag-info-replication">
            <div class="coral-ColumnView-preview-label tag-info-label"><%=i18n.get("PUBLISHED")%></div>
            <div class="tag-info-detail-minor">
                <div class="tag-info-detail-minor-left tag-info-replicated"></div>
                <div class="coral-ColumnView-preview-label tag-info-detail-minor-right tag-info-replicatedby"></div>
            </div>
        </div>

        <div class="tag-info-description">
            <div class="coral-ColumnView-preview-label tag-info-label"><%=i18n.get("DESCRIPTION")%></div>
            <div class="coral-ColumnView-preview-value tag-info-description-value"></div>
        </div>
  </div>

  <div class="tagref-details">
      <div class="tagref-details-back coral-ColumnView-item coral-ColumnView-item--back">
        <%=i18n.get("References")%>
      </div>
      <div class="tagref-details-list">
          <section class="tagref-item">
              <div class="tagref-info">
                  <span class="tagref-title"></span>
                  <div class="tagref-path"></div>
              </div>
          </section>
      </div>
      <div class="tagref-details-toolbars"></div>
  </div>
  <ui:includeClientLib categories="cq.tagging.touch.sidepanels.taginfo" />
