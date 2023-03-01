package com.tastik.cycal.data.message;

import com.tastik.cycal.core.domain.Flags;
import com.tastik.cycal.core.domain.Race;
import com.tastik.cycal.core.domain.RaceDay;
import com.tastik.cycal.core.domain.IndividualRankingPosition;
import com.tastik.cycal.core.domain.Report;
import com.tastik.cycal.core.domain.TeamRankingPosition;
import com.tastik.cycal.core.interactors.ReportSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Component
public class TelegramSender implements ReportSender {
    @Value("${telegram.bot.token}")
    private String TELEGRAM_BOT_TOKEN;
    @Value("${telegram.channel.id}")
    private String TELEGRAM_CHANNEL_ID;

    @Value("${telegram.host:https://api.telegram.org}")
    private String HOST;

    RestTemplate restTemplate;

    public TelegramSender() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(Report report) {
        try {
            String urlString = sendTelegramMessageUrl();

            StringBuilder messageContent = new StringBuilder();

            formatTodayRacesDataWith(report, messageContent);

            formatRankingDataWith(report, messageContent);

            urlString = String.format(
                    urlString,
                    TELEGRAM_BOT_TOKEN,
                    TELEGRAM_CHANNEL_ID,
                    escapeCharactersOf(messageContent)
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
//            final var response = restTemplate.exchange(url.toString(), HttpMethod.GET, null, String.class);

//            UriComponentsBuilder telegramRequestBuilder = UriComponentsBuilder.fromHttpUrl(
//                    String.format("https://api.telegram.org/bot%s/sendMessage", TELEGRAM_BOT_TOKEN))
//                    .queryParam("chat_id", TELEGRAM_CHANNEL_ID)
//                    .queryParam("text", format(messageContent))
//                    .queryParam("parse_mode", "MarkdownV2");
//
//            ResponseEntity<String> response
//                    = restTemplate.getForEntity(telegramRequestBuilder.toUriString(), String.class);
//            final var response = restTemplate.postForObject(url.toString(), null, String.class);

            LOG.info("Telegram response: {}", response/*.getBody()*/);

        } catch (Exception ex) {
            LOG.error("Error Sending Message {}", ex.getMessage());
        }
    }

    private static void formatTodayRacesDataWith(Report report, StringBuilder messageContent) {
        try {
            messageContent
                    .append("*ROAD RACES TODAY:*")
                    .append(NEW_LINE);
            report.races().todayRaces().forEach(
                    race -> {
                        if (race.isMultiDayRace()) {
                            formatMultiDayRaceDataWith(messageContent, race);
                        }

                        if (race.isOneDayRace()) {
                            formatOneDayRaceDataWith(messageContent, race);
                        }
                    }
            );
        } catch (Exception ex) {
            LOG.error("There was a problem formatting TODAY RACES DATA: {}", ex.getMessage());
        }
    }

    private static void formatOneDayRaceDataWith(StringBuilder messageContent, Race race) {
        try {
            raceLineWith(messageContent, race);
        } catch (Exception ex) {
            LOG.error("Unable to format data of race {}: {}", race.name(), ex.getMessage());
        }
    }

    private static void formatMultiDayRaceDataWith(StringBuilder messageContent, Race race) {
        try {
            final var todayItem = race.properties().schedule().items().stream().filter(item -> item.date().equals(LocalDate.now())).findFirst();
            if (todayItem.isPresent()) {
                raceLineWith(messageContent, race);
                if (race.isNationalChampionship()) {
                    formatNationalChampionshipDataWith(messageContent, todayItem);
                } else {
                    formatGrandTourDataWith(messageContent, race, todayItem);
                }
            }
        } catch (Exception ex) {
            LOG.error("Unable to format data of race {}: {}", race.name(), ex.getMessage());
        }
    }

    private static void raceLineWith(StringBuilder messageContent, Race race) {
        messageContent.append(flagOrDefault(race)).append(" ").append(race.name());
        if (race.hasUrl()) {
            messageContent.append(" ").append("[➕]").append("(").append(race.url()).append(")");
        }
        messageContent.append(NEW_LINE);
    }

    private static void formatNationalChampionshipDataWith(StringBuilder messageContent, Optional<RaceDay> todayItem) {
        todayItem.get().races().forEach(r -> {
            messageContent.append(TAB).append("🚴").append(" ").append(r.raceName());
            messageContent.append(NEW_LINE);
        });
    }

    private static void formatGrandTourDataWith(StringBuilder messageContent, Race race, Optional<RaceDay> todayItem) {
        messageContent.append(TAB)
                .append("🚴").append(" ")
                .append(todayItem.get().races().stream().findFirst().orElseThrow().raceName())
                .append("/").append(race.properties().schedule().items().size());
        messageContent.append(NEW_LINE);
    }

    private void formatRankingDataWith(Report report, StringBuilder messageContent) {
        messageContent.append("-------------------")
                .append(NEW_LINE)
                .append("*INDIVIDUAL RANKING UCI (points):*");
        individualMenRanking(report, messageContent);
        individualWomenRanking(report, messageContent);

        messageContent.append("-------------------")
                .append(NEW_LINE)
                .append("*TEAM RANKING UCI (points):*");
        teamMenRanking(report, messageContent);
        teamWomenRanking(report, messageContent);
    }

    private void individualMenRanking(Report report, StringBuilder messageContent) {
        try {
            final var podiumMen = report.ranking().individualRanking()
                    .mens()
                    .individualResults()
                    .stream()
                    .sorted(Comparator.comparingDouble(IndividualRankingPosition::totalPoints).reversed())
                    .filter(distinctByKey(IndividualRankingPosition::individualName))
                    .limit(3)
                    .toList();

            messageContent
                    .append(NEW_LINE)
                    .append("*Men:*")
                    .append(NEW_LINE);


            IntStream.range(0, podiumMen.size()).forEach(index -> {
                IndividualRankingPosition rider = podiumMen.get(index);
                messageContent.append(rankingEmojiBy(index + 1)).append(rider.firstName()).append(" ").append(rider.lastName())
                        .append(": ").append(rider.totalPoints()).append(NEW_LINE);
            });
        } catch (Exception ex) {
            LOG.error("There was a problem formatting INDIVIDUAL MEN RANKING DATA: {}", ex.getMessage());
        }
    }

    private void individualWomenRanking(Report report, StringBuilder messageContent) {
        try {
            final var podiumWomen = report.ranking().individualRanking()
                    .womens()
                    .individualResults()
                    .stream()
                    .sorted(Comparator.comparingDouble(IndividualRankingPosition::totalPoints).reversed())
                    .filter(distinctByKey(IndividualRankingPosition::individualName))
                    .limit(3)
                    .toList();

            messageContent
                    .append(NEW_LINE)
                    .append("*Women:*")
                    .append(NEW_LINE);

            IntStream.range(0, podiumWomen.size()).forEach(index -> {
                IndividualRankingPosition rider = podiumWomen.get(index);
                messageContent.append(rankingEmojiBy(index + 1)).append(rider.firstName()).append(" ").append(rider.lastName())
                        .append(": ").append(rider.totalPoints()).append(NEW_LINE);
            });
        } catch (Exception ex) {
            LOG.error("There was a problem formatting INDIVIDUAL WOMEN RANKING DATA: {}", ex.getMessage());
        }
    }

    private void teamMenRanking(Report report, StringBuilder messageContent) {
        try {
            final var podiumTeamMen = report.ranking().teamRanking()
                    .mens()
                    .teamResults()
                    .stream()
                    .sorted(Comparator.comparingDouble(TeamRankingPosition::totalPoints).reversed())
                    .filter(distinctByKey(TeamRankingPosition::teamName))
                    .limit(3)
                    .toList();

            messageContent
                    .append(NEW_LINE)
                    .append("*Men:*")
                    .append(NEW_LINE);

            IntStream.range(0, podiumTeamMen.size()).forEach(index -> {
                TeamRankingPosition team = podiumTeamMen.get(index);
                messageContent.append(rankingEmojiBy(index + 1)).append(team.teamName())
                        .append(": ").append(team.totalPoints()).append(NEW_LINE);
            });
        } catch (Exception ex) {
            LOG.error("There was a problem formatting TEAM MEN RANKING DATA: {}", ex.getMessage());
        }
    }

    private void teamWomenRanking(Report report, StringBuilder messageContent) {
        try {
            final var podiumTeamWomen = report.ranking().teamRanking()
                    .womens()
                    .teamResults()
                    .stream()
                    .sorted(Comparator.comparingDouble(TeamRankingPosition::totalPoints).reversed())
                    .filter(distinctByKey(TeamRankingPosition::teamName))
                    .limit(3)
                    .toList();

            messageContent
                    .append(NEW_LINE)
                    .append("*Women:*")
                    .append(NEW_LINE);

            IntStream.range(0, podiumTeamWomen.size()).forEach(index -> {
                TeamRankingPosition team = podiumTeamWomen.get(index);
                messageContent.append(rankingEmojiBy(index + 1)).append(team.teamName())
                        .append(": ").append(team.totalPoints()).append(NEW_LINE);
            });
        } catch (Exception ex) {
            LOG.error("There was a problem formatting TEAM WOMEN RANKING DATA: {}", ex.getMessage());
        }
    }

    private static String flagOrDefault(Race race) {
        return Flags.get(race.country());
    }

    private String rankingEmojiBy(int i) {
        var emoji = new String();
        switch (i) {
            case 1:
                emoji = MEDAL_ONE;
                break;
            case 2:
                emoji = MEDAL_TWO;
                break;
            default:
                emoji = MEDAL_THREE;
        }
        return emoji;
    }

    private static String escapeCharactersOf(StringBuilder message) {
//        return URLEncoder.encode(message.toString(), StandardCharsets.UTF_8)
        return message.toString()
                .replace("-", "\\-")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace(".", "\\.")
                .replace("#", "\\#")
                .replace("`", "\\`")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("_", "\\_")
                .replace(">", "\\>")
                .replace("+", "\\+");
//                            .replace("*", "\\*")
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private String sendTelegramMessageUrl() {
        return HOST + SEND_MESSAGE_WITH_PARAMS;
    }

    public static final String SEND_MESSAGE_WITH_PARAMS = "/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=MarkdownV2";
    private static final String MEDAL_ONE = "%F0%9F%A5%87";
    private static final String MEDAL_TWO = "%F0%9F%A5%88";
    private static final String MEDAL_THREE = "%F0%9F%A5%89";
    private static final String NEW_LINE = "%0A";
    private static final String TAB = "%20%20%20%20";
    private static final Logger LOG = LoggerFactory.getLogger(TelegramSender.class);
}
