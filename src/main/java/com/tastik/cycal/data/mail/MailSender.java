package com.tastik.cycal.data.mail;

import com.tastik.cycal.core.domain.Races;
import com.tastik.cycal.core.interactors.RacesSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Component
public class MailSender implements RacesSender {
    @Value("${telegram.bot.token}")
    private String TELEGRAM_BOT_TOKEN;
    @Value("${telegram.channel.id}")
    private String TELEGRAM_CHANNEL_ID;

    @Override
    public void send(Races races) {
        try {
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=MarkdownV2";

            StringBuilder messageContent = new StringBuilder();
            messageContent.append("*ROAD RACES TODAY:*");
            messageContent.append("");
            races.races().forEach(
                    race -> messageContent
                            .append("- ")
                            .append(race.name())
                            .append(" (")
                            .append(race.dates())
                            .append(").")
                            .append("")
            );
            urlString = String.format(
                    urlString,
                    TELEGRAM_BOT_TOKEN,
                    TELEGRAM_CHANNEL_ID,
                    messageContent.toString()
                            .replace("-", "\\-")
                            .replace("(", "\\(")
                            .replace(")", "\\)")
                            .replace(".", "\\.")
//                            .replace("*", "\\*")
            );
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            String response = sb.toString();
            LOG.info("Telegram response: {}", response);

        } catch (Exception ex) {
            LOG.error("Error Sending Email {}", ex.getMessage());
        }
    }
    private static final Logger LOG = LoggerFactory.getLogger(MailSender.class);
}
