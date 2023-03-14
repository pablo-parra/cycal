package com.tastik.cycal.data.message;

import com.tastik.cycal.core.config.Flags;
import com.tastik.cycal.core.domain.rankings.IndividualRankingPosition;
import com.tastik.cycal.core.domain.races.Race;
import com.tastik.cycal.core.domain.races.RaceDay;
import com.tastik.cycal.core.domain.report.Report;
import com.tastik.cycal.core.domain.races.Stage;
import com.tastik.cycal.core.domain.races.StageResults;
import com.tastik.cycal.core.domain.rankings.TeamRankingPosition;
import com.tastik.cycal.core.domain.results.RaceResult;
import com.tastik.cycal.core.interactors.ReportSender;
import com.tastik.cycal.core.interactors.ResultsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

@Component
public class TelegramSender implements ReportSender {
    private static final String GENERAL_CLASSIFICATION = "General Classification";
    private static final String STAGE_CLASSIFICATION = "Stage Classification";
    private static final String FINAL_CLASSIFICATION = "Final Classification";
    @Value("${telegram.bot.token}")
    private String TELEGRAM_BOT_TOKEN;
    @Value("${telegram.channel.id}")
    private String TELEGRAM_CHANNEL_ID;
    @Value("${telegram.links.enabled}")
    private boolean TELEGRAM_LINKS_ENABLED;
    @Value("${telegram.general.classification.positions.show:5}")
    private int generalClassificationPositions;
    @Value("${telegram.host:https://api.telegram.org}")
    private String HOST;
    RestTemplate restTemplate;

    ResultsReader resultsReader;

    public TelegramSender(@Autowired ResultsReader resultsReader) {
        this.restTemplate = new RestTemplate();
        this.resultsReader = resultsReader;
    }

    @Override
    public void send(Report report) {
        try {
            String urlString = sendTelegramMessageUrl();

            StringBuilder messageContent = new StringBuilder();

            formatYesterdayResults(report, messageContent);

            messageContent.append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);

            formatTodayRacesDataWith(report, messageContent);

            messageContent.append(NEW_LINE).append(NEW_LINE).append(NEW_LINE);

            formatRankingDataWith(report, messageContent);

            urlString = String.format(
                    urlString,
                    TELEGRAM_BOT_TOKEN,
                    TELEGRAM_CHANNEL_ID,
                    escapeCharactersOf(messageContent)
            );


//            URL url = new URL(urlString);
//            URLConnection conn = url.openConnection();
//            StringBuilder sb = new StringBuilder();
//            InputStream is = new BufferedInputStream(conn.getInputStream());
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            String inputLine = "";
//            while ((inputLine = br.readLine()) != null) {
//                sb.append(inputLine);
//            }
//            String response = sb.toString();

            // -------------------------------------

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("curl", "-X", "GET", urlString);
            Process process = processBuilder.start();

            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(process.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            String response = sb.toString();

            int exitCode = process.waitFor();
            LOG.info("CURL exit code: {}", exitCode);
            process.destroy();

            // -------------------------------------

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
            LOG.error("Error Sending Message to TELEGRAM: {}", ex.getMessage());
        }
    }

    private void formatYesterdayResults(Report report, StringBuilder messageContent) {
        try {
            final var results = new StringBuilder();

            report.races().yesterdayRaces().forEach(
                    race -> {
                        if (race.isMultiDayRace()) {
                            formatMultiDayRaceResultsWith(results, race);
                        }
                        if (race.isOneDayRace()) {
                            formatOneDayRaceResultsWith(results, race);
                        }
                    }
            );

            if (!results.isEmpty()) {
                results.insert(0, NEW_LINE);
                results.insert(0, "*YESTERDAY RACES RESULTS:*");
            }

            messageContent.append(results);

        } catch (Exception ex) {
            LOG.error("There was a problem formatting TODAY RACES DATA: {}", ex.getMessage());
        }
    }

    private void formatMultiDayRaceResultsWith(StringBuilder results, Race race) {
        try {
            final var yesterdayStage = race.yesterdayStage();
            if (yesterdayStage.isPresent()) {
                raceLineWith(results, race);
                if (race.isNationalChampionship()) {
                    formatNationalChampionshipResultsWith(results, race, yesterdayStage.get());
                } else {
                    formatGrandTourResultsWith(results, race, yesterdayStage.get());
                }
            }
        } catch (Exception ex) {
            LOG.error("There was a problem formatting RESULTS DATA of race {}: {}", race.name(), ex.getMessage());
        }
    }

    private void formatOneDayRaceResultsWith(StringBuilder results, Race race) {
        try {
            final var yesterdayStage = race.yesterdayStage();
            if (yesterdayStage.isPresent()) {
                raceLineWith(results, race);

                yesterdayStage.get().races().forEach(competition -> {
                    final var dayResults = race.properties().results().accordion().stream().filter(item -> item.label().equals(competition.raceName())).findFirst();

                    if (dayResults.isPresent()) {
                        final var finalClassificationResult = dayResults.get().results().stream().filter(result -> GENERAL_CLASSIFICATION.equals(result.title())).findFirst().orElseThrow();
                        final var finalClassificationCode = finalClassificationResult.eventCode();
                        final var finalClassification = resultsReader.readRaceResults(finalClassificationCode);
                        if (finalClassification.isPresent()) {
                            final var finalResults = finalClassification.get().results();
                            for (int i = 0; i < 3/*podium.positions().size()*/; i++) {
                                results.append(rankingEmojiBy(Integer.parseInt(finalResults.get(i).values().rank())))
                                        .append(finalResults.get(i).values().firstname())
                                        .append(" ")
                                        .append(finalResults.get(i).values().lastname())
                                        .append(" ")
                                        .append(calculateTime(finalResults, i))
                                        .append(NEW_LINE);
                            }
                            results.append(NEW_LINE);
                        }
                    }
                });
            }
        } catch (Exception ex) {
            LOG.error("There was a problem formatting RESULTS DATA of race {}: {}", race.name(), ex.getMessage());
        }
    }

    private void formatGrandTourResultsWith(StringBuilder results, Race race, RaceDay raceDay) {
        raceDay.races().forEach(competition -> {
            if (competition.isFinalClassification()) return;

            final var dayResults = race.properties().results().accordion().stream().filter(item -> item.label().equals(competition.raceName())).findFirst();
            if (dayResults.isPresent()) {
                formatStageWinner(results, race, competition, dayResults.get());
                if (!raceIsFinished(raceDay)) formatGeneralClassification(results, race, dayResults.get());
            } else {
                LOG.info("No results found in 'results' object for {}: {}", race.name(), competition.raceName());
            }
        });

        if (raceIsFinished(raceDay)) {
            formatFinalClassification(results, race);
        }
    }

    private void formatFinalClassification(StringBuilder results, Race race) {
        final var finalResults = race.properties().results().accordion().stream().filter(item -> FINAL_CLASSIFICATION.equals(item.label())).findFirst();
        finalResults.ifPresent(stageResults -> formatGeneralClassification(results, race, stageResults));
    }

    private boolean raceIsFinished(RaceDay raceDay) {
        return raceDay.races().stream().anyMatch(Stage::isFinalClassification);
    }

    private void formatStageWinner(StringBuilder results, Race race, Stage competition, StageResults dayResults) {
        try {
            final var stageClassificationResult = dayResults.results().stream().filter(result -> STAGE_CLASSIFICATION.equals(result.title())).findFirst().orElseThrow();
            final var stageClassificationCode = stageClassificationResult.eventCode();
            final var stageClassification = resultsReader.readRaceResults(stageClassificationCode);

            results.append(TAB)
                    .append(competition.raceName()).append(":");

            if (stageClassification.isPresent() && !stageClassification.get().results().isEmpty() && nonNull(stageClassification.get().podium())) {
                final var winner = stageClassification.get().first().orElseThrow();
                results.append(" ").append("🏆")
                        .append(" ").append(winner.firstname()).append(" ").append(winner.lastname()).append(" ")
                        .append(NEW_LINE);
            } else {
                LOG.info("No results available for {} of {} (EVENT CODE: {})", competition.raceName(), race.name(), stageClassificationCode);
                results.append(TAB)
                        .append(" ")
                        .append("No results available ☹️")
                        .append(NEW_LINE);
            }

        } catch (Exception ex) {
            LOG.error("There was a problem formatting the WINNER of the {} of {}: {}", competition.raceName(), race.name(), ex.getMessage());
        }
    }

    private void formatGeneralClassification(StringBuilder results, Race race, StageResults dayResults) {
        try {
            final var generalClassificationReference = dayResults.results().stream().filter(result -> Objects.nonNull(result.title()) && result.title().contains(GENERAL_CLASSIFICATION)).findFirst().orElseThrow();
            final var generalClassificationCode = generalClassificationReference.eventCode();
            final var generalClassificationResults = resultsReader.readRaceResults(generalClassificationCode);
            results.append("⭐️").append(" G.C.:").append(NEW_LINE);
            if (generalClassificationResults.isPresent() && !generalClassificationResults.get().results().isEmpty() && nonNull(generalClassificationResults.get().podium())) {

                final var generalClassification = generalClassificationResults.get().results();

                for (int i = 0; i < generalClassificationPositions; i++) {
                    results.append(TAB)
                            .append(rankingEmojiBy(Integer.parseInt(generalClassification.get(i).values().rank())))
                            .append(generalClassification.get(i).values().firstname())
                            .append(" ")
                            .append(generalClassification.get(i).values().lastname())
                            .append(" ")
                            .append(calculateTime(generalClassification, i))
                            .append(NEW_LINE);
                }
                results.append(NEW_LINE);
            } else {
                LOG.info("No GENERAL CLASSIFICATION results available for {} (EVENT CODE: {})", race.name(), generalClassificationCode);
                results.append(TAB)
                        .append(" ")
                        .append("No results available ☹️")
                        .append(NEW_LINE);
            }
        } catch (Exception ex) {
            LOG.error("There was a problem formatting the GENERAL CLASSIFICATION of {}: {}", race.name(), ex.getMessage());
        }
    }

    private static String calculateTime(List<RaceResult> generalClassification, int i) {
        final var time = generalClassification.get(i).values();

        final var winnerTime = generalClassification.get(0).values().result().trim();

        if (isTheWinner(i)) return time.result();

        if (time.hasDifferenceAlreadyCalculated()) return time.startingWithAPlus();

        return time.differenceWith(winnerTime);
    }

    private static boolean isTheWinner(int i) {
        return i == 0;
    }

    private void formatNationalChampionshipResultsWith(StringBuilder results, Race race, RaceDay raceDay) {
        raceDay.races().forEach(competition -> {
            final var dayResults = race.properties().results().accordion().stream().filter(item -> item.label().equals(competition.raceName())).findFirst();

            if (dayResults.isPresent()) {
                final var generalClassificationResult = dayResults.get().results().stream().filter(result -> "General Classification".equals(result.title())).findFirst().orElseThrow();
                final var generalClassificationCode = generalClassificationResult.eventCode();
                final var generalClassification = resultsReader.readRaceResults(generalClassificationCode);
                if (generalClassification.isPresent()) {
                    final var podium = generalClassification.get().calculatePodium();

                    for (int i = 0; i < podium.positions().size(); i++) {
                        results.append(TAB)
                                .append(rankingEmojiBy(podium.positions().get(i).position()))
                                .append(podium.positions().get(i).firstName())
                                .append(" ")
                                .append(podium.positions().get(i).lastName())
                                .append(" ")
                                .append(podium.positions().get(i).time())
                                .append(NEW_LINE);
                    }
                    results.append(NEW_LINE);
                }
            } else {
                LOG.info("No results found in 'results' object for {}: {}", race.name(), competition.raceName());
            }
        });

    }

    private void formatTodayRacesDataWith(Report report, StringBuilder messageContent) {
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

    private void formatMultiDayRaceDataWith(StringBuilder messageContent, Race race) {
        try {
            final var todayStage = race.todayStage();
            if (todayStage.isPresent()) {
                raceLineWith(messageContent, race);
                if (race.isNationalChampionship()) {
                    formatNationalChampionshipDataWith(messageContent, todayStage);
                } else {
                    formatGrandTourDataWith(messageContent, race, todayStage);
                }
            }
        } catch (Exception ex) {
            LOG.error("Unable to format data of race {}: {}", race.name(), ex.getMessage());
        }
    }

    private void formatOneDayRaceDataWith(StringBuilder messageContent, Race race) {
        try {
            raceLineWith(messageContent, race);
        } catch (Exception ex) {
            LOG.error("Unable to format data of race {}: {}", race.name(), ex.getMessage());
        }
    }

    private void raceLineWith(StringBuilder messageContent, Race race) {
        messageContent.append(flagOrDefault(race)).append(" ").append(race.name());
        if (TELEGRAM_LINKS_ENABLED && race.hasUrl()) {
            messageContent.append(" ").append("[➕]").append("(").append(race.url()).append(")");
        }
        messageContent.append(":");
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
                .append(todayItem.get().races().stream().filter(Stage::isStageRace).findFirst().orElseThrow().raceName())
                .append("/").append(race.properties().schedule().items().size());
        messageContent.append(NEW_LINE).append(NEW_LINE);
    }

    private void formatRankingDataWith(Report report, StringBuilder messageContent) {
        if (isMonday()) {
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
    }

    private static boolean isMonday() {
        return LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY);
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
            case 3:
                emoji = MEDAL_THREE;
                break;
            case 4:
                emoji = "4️⃣";
                break;
            case 5:
                emoji = "5️⃣";
                break;
            default:
                emoji = "⚪️";
        }
        return emoji;
    }

    private static String escapeCharactersOf(StringBuilder message) {
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
                .replace("+", "\\+")
                .replace("&", "%26");
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
