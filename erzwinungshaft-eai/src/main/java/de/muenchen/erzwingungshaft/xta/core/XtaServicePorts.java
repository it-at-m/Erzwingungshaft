package de.muenchen.erzwingungshaft.xta.core;

import genv3.de.xoev.transport.xta.x211.ManagementPortType;
import genv3.de.xoev.transport.xta.x211.MsgBoxPortType;
import genv3.de.xoev.transport.xta.x211.SendPortType;

public record XtaServicePorts(

        /*
         * used for: createMessageId, cancelMessage, getTransportReport, lookupService, checkAccountActive
         */
        ManagementPortType managementPortType,

        /*
         * used for: close, getNextStatusList, getNextMessage, getStatusList, getMessage
         */
        MsgBoxPortType msgBoxPortType,

        /*
         * used for: sendMessageSync, sendMessage
         */
        SendPortType sendPortType
){
}
