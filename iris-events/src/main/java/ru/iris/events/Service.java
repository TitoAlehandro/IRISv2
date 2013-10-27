package ru.iris.events;

import org.apache.log4j.xml.DOMConfigurator;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.iris.common.Config;
import ru.iris.common.I18N;
import ru.iris.common.Messaging;
import ru.iris.common.SQL;

import javax.jms.*;
import java.util.HashMap;

/**
 * IRISv2 Project
 * Author: Nikolay A. Viguro
 * WWW: iris.ph-systems.ru
 * E-Mail: nv@ph-systems.ru
 * Date: 02.10.13
 * Time: 16:32
 */

public class Service
{
    public static HashMap<String, String> config;
    public static SQL sql;
    public static MessageConsumer messageConsumer;
    public static MessageProducer messageProducer;
    @NonNls
    public static Messaging msg;
    public static Session session;
    private static I18N i18n = new I18N();

    private static Logger log = LoggerFactory.getLogger (Service.class);

    public static void main(String[] args) throws Exception {

        DOMConfigurator.configure("conf/etc/log4j.xml");

        Config cfg = new Config ();
        config = cfg.getConfig ();
        sql = new SQL ();

        msg = new Messaging ();
        messageConsumer = msg.getConsumer ();
        messageProducer = msg.getProducer ();
        session = msg.getSession ();

        log.info ("[iris] ----------------------------------");
        log.info (i18n.message("iris.events.engine.starting"));
        log.info ("[iris] ----------------------------------");


        // Check module status

        Message mess;
        @NonNls MapMessage m = null;

        msg.simpleSendMessage("status.answer", "alive", "events");

        while ((mess = Service.messageConsumer.receive (0)) != null)
        {
            m = (MapMessage) mess;

            if(m.getStringProperty("qpid.subject").equals ("status.events") || m.getStringProperty("qpid.subject").equals ("status.all"))
            {
                log.info (i18n.message("events.got.status.query"));
                msg.simpleSendMessage("status.answer", "alive", "events");
            }
        }


    }
}
