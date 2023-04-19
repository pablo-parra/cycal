package com.tastik.cycal.core.domain;

import com.tastik.cycal.core.config.Gender;
import com.tastik.cycal.core.config.RaceCategory;
import com.tastik.cycal.core.domain.races.CompetitionDetails;
import com.tastik.cycal.core.domain.races.CompetitionProperties;
import com.tastik.cycal.core.domain.races.Race;
import com.tastik.cycal.core.domain.races.RaceDay;
import com.tastik.cycal.core.domain.races.Schedule;
import com.tastik.cycal.core.domain.races.Stage;
import com.tastik.cycal.core.domain.races.Website;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RaceTests {

    @Test
    void is_one_day_race() {
        final var oneDayRace = oneDayRace();
        assertThat(oneDayRace.isOneDayRace()).isTrue();
        assertThat(oneDayRace.isMultiDayRace()).isFalse();
    }

    @Test
    void is_multi_day_race() {
        final var multiDayRace = multiDayRace();
        assertThat(multiDayRace.isMultiDayRace()).isTrue();
        assertThat(multiDayRace.isOneDayRace()).isFalse();
    }

    @Test
    void is_National_Championship() {
        final var nationalChampionship = nationalChampionship();
        final var nationalChampionships = nationalChampionships();
        assertThat(nationalChampionship.isNationalChampionship()).isTrue();
        assertThat(nationalChampionships.isNationalChampionship()).isTrue();
    }

    @Test
    void is_women_race() {
        final var womenRace = womenRace();
        assertThat(womenRace.isWomenRace()).isTrue();
        assertThat(womenRace.isMenRace()).isFalse();
        assertThat(womenRace.gender()).isEqualTo(Gender.WOMEN);
    }

    @Test
    void is_men_race() {
        final var menRace = menRace();
        assertThat(menRace.isMenRace()).isTrue();
        assertThat(menRace.isWomenRace()).isFalse();
        assertThat(menRace.gender()).isEqualTo(Gender.MEN);
    }

    @Test
    void is_mixed_race() {
        final var mixedRace = mixedRace();
        assertThat(mixedRace.isWomenRace()).isFalse();
        assertThat(mixedRace.isMenRace()).isFalse();
        assertThat(mixedRace.gender()).isEqualTo(Gender.MIXED);
    }

    @Test
    void race_has_url() {
        final var raceWithUrl = raceWithUrl();
        assertThat(raceWithUrl.hasUrl()).isTrue();

        final var raceWithOutUrl = oneDayRace();
        assertThat(raceWithOutUrl.hasUrl()).isFalse();
    }

    @Test
    void is_today_race() {
        final var raceWithStageToday = raceWithStageToday();
        assertThat(raceWithStageToday.todayStage()).isPresent();
        assertThat(raceWithStageToday.yesterdayStage()).isEmpty();

        final var raceWithStageYesterday = raceWithStageYesterday();
        assertThat(raceWithStageYesterday.todayStage()).isEmpty();
        assertThat(raceWithStageYesterday.yesterdayStage()).isPresent();
    }

    @Test
    void race_with_unknown_category_has_expected_category() {
        final var raceWithCategoryUnknown = raceCategoryUnknown();
        assertThat(raceWithCategoryUnknown.category()).isEqualTo(RaceCategory.UNKNOWN);
    }

    @Test
    void race_without_category_has_expected_category() {
        final var raceWithoutCategory = raceWithoutCategory();
        assertThat(raceWithoutCategory.category()).isEqualTo(RaceCategory.UNKNOWN);
    }

    @Test
    void race_with_category_has_expected_category() {
        final var raceWithCategoryUWT = raceCategoryUWT();
        assertThat(raceWithCategoryUWT.category()).isEqualTo(RaceCategory.UWT);
    }

    private Race oneDayRace() {
        return new Race(
                ONE_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        ONE_DAY_RACE,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race multiDayRace() {
        return new Race(
                MULTI_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        MULTI_DAY_RACE,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), Collections.emptyList()),
                                        new RaceDay(LocalDate.now(), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race nationalChampionship() {
        return new Race(
                NATIONAL_CHAMPIONSHIP,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        NATIONAL_CHAMPIONSHIP,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race nationalChampionships() {
        return new Race(
                NATIONAL_CHAMPIONSHIPS,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        NATIONAL_CHAMPIONSHIP,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race womenRace() {
        return new Race(
                MULTI_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        MULTI_DAY_RACE,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), List.of(
                                                new Stage(SOME_WOMEN_RACE, null, null, SOME_WOMEN_RACE))
                                        ),
                                        new RaceDay(LocalDate.now(), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race menRace() {
        return new Race(
                MULTI_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        MULTI_DAY_RACE,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), List.of(
                                                new Stage(SOME_MEN_RACE, null, null, SOME_MEN_RACE))
                                        ),
                                        new RaceDay(LocalDate.now(), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race mixedRace() {
        return new Race(
                MULTI_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        MULTI_DAY_RACE,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), List.of(
                                                new Stage(SOME_MEN_RACE, null, null, SOME_MEN_RACE)
                                        )),
                                        new RaceDay(LocalDate.now(), List.of(
                                                new Stage(SOME_WOMEN_RACE, null, null, SOME_WOMEN_RACE)
                                        ))
                                )
                        ),
                        null
                )
        );
    }

    private Race raceWithUrl() {
        return new Race(
                ONE_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        ONE_DAY_RACE,
                        new CompetitionDetails(ONE_DAY_RACE, null, null, ESP, null, new Website(null, SOME_URL, false), null),
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race raceWithStageToday() {
        return new Race(
                ONE_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        ONE_DAY_RACE,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now(), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race raceWithStageYesterday() {
        return new Race(
                ONE_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        ONE_DAY_RACE,
                        null,
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now().minusDays(1), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race raceCategoryUnknown() {
        return new Race(
                ONE_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        ONE_DAY_RACE,
                        new CompetitionDetails(null, null, null, ESP, "some unknown category", null, null),
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now().minusDays(1), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race raceWithoutCategory() {
        return new Race(
                ONE_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        ONE_DAY_RACE,
                        new CompetitionDetails(null, null, null, ESP, null, null, null),
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now().minusDays(1), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private Race raceCategoryUWT() {
        return new Race(
                ONE_DAY_RACE,
                null,
                null,
                ESP,
                null,
                null,
                new CompetitionProperties(
                        ONE_DAY_RACE,
                        new CompetitionDetails(null, null, null, ESP, UWT, null, null),
                        new Schedule(
                                List.of(
                                        new RaceDay(LocalDate.now().minusDays(1), Collections.emptyList())
                                )
                        ),
                        null
                )
        );
    }

    private static final String ONE_DAY_RACE = "one-day-race";
    private static final String MULTI_DAY_RACE = "multi-day-race";
    private static final String NATIONAL_CHAMPIONSHIP = "Some National Championship";
    private static final String NATIONAL_CHAMPIONSHIPS = "Some National Championships";
    public static final String SOME_WOMEN_RACE = "Some Women race";
    public static final String SOME_MEN_RACE = "Some Men race";
    public static final String SOME_URL = "some/url/of/the/race";
    public static final String UWT = ".UWT";
    private static final String ESP = "ESP";
}
