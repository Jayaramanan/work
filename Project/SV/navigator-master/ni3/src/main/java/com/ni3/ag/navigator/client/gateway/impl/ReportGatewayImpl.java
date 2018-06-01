package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.ReportFormat;
import com.ni3.ag.navigator.client.gateway.ReportGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.Reports;
import com.ni3.ag.navigator.shared.proto.NRequest.Reports.Builder;

public class ReportGatewayImpl extends AbstractGatewayImpl implements ReportGateway{
	@Override
	public byte[] getReport(Integer id, ReportFormat reportFormat, byte[] graphImage, byte[] mapImage, byte[] logoImage,
	        List<NRequest.ReportData> data) {
        final Builder builder = NRequest.Reports.newBuilder();
        builder.setAction(NRequest.Reports.Action.GET_PRINT);
        NRequest.Report.Builder reportRequest = NRequest.Report.newBuilder();
        reportRequest.setId(id);
        switch (reportFormat) {
            case HTML:
                reportRequest.setReportFormat(NRequest.Report.ReportFormat.HTML);
                break;
            case PDF:
                reportRequest.setReportFormat(NRequest.Report.ReportFormat.PDF);
                break;
            case XLS:
                reportRequest.setReportFormat(NRequest.Report.ReportFormat.XLS);
                break;
        }

        if (graphImage != null) {
            reportRequest.setGraphImage(ByteString.copyFrom(graphImage));
        }
        if (mapImage != null) {
            reportRequest.setMapImage(ByteString.copyFrom(mapImage));
        }
        if (logoImage != null) {
            reportRequest.setLogoImage(ByteString.copyFrom(logoImage));
        }
        if (data != null) {
            reportRequest.addAllData(data);
        }
        builder.setReport(reportRequest);

        final Reports request = builder.build();
        NResponse.Reports response;
        try {
            ByteString payload = sendRequest(ServletName.ReportProvider, request);
            response = NResponse.Reports.parseFrom(payload);
        } catch (IOException e) {
            showErrorAndThrow("No connection to server", e);
            return null;
        }

        byte[] result = null;
        if (response != null && response.getReportPrint() != null) {
            result = response.getReportPrint().toByteArray();
        }
        return result;
    }

	@Override
	public List<NResponse.Report> getReportTemplates() {
        final Builder builder = NRequest.Reports.newBuilder();
        builder.setAction(NRequest.Reports.Action.GET_ALL);
        final Reports request = builder.build();
        NResponse.Reports reports = null;
        try {
            ByteString payload = sendRequest(ServletName.ReportProvider, request);
            reports = NResponse.Reports.parseFrom(payload);
        } catch (IOException e) {
            showErrorAndThrow("No connection to server", e);
        }

        return reports != null ? reports.getReportsList() : new ArrayList<NResponse.Report>();
    }

}
