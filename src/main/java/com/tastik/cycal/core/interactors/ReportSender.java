package com.tastik.cycal.core.interactors;

import com.tastik.cycal.core.domain.Report;

public interface ReportSender {
    void send(Report races);
}
