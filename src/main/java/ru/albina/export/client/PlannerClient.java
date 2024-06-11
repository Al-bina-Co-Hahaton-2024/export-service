package ru.albina.export.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.albina.backlib.configuration.WebConstants;
import ru.albina.export.dto.schedule.DayWorkSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class PlannerClient {

    private final WebClient webClient;

    public PlannerClient(WebClient.Builder libWebClientBuilder) {
        this.webClient = libWebClientBuilder
                .baseUrl(Optional.ofNullable(System.getenv("PLANNER_SERVICE_HOST")).orElse("http://localhost:8084"))
                .build();
    }


    public List<DayWorkSchedule> getScheduler(LocalDate date) {
        return this.webClient.get()
                .uri(urlBuilder -> urlBuilder.path(
                                        WebConstants.FULL_PRIVATE + "/work-schedules"
                                )
                                .queryParam("date", date)
                                .build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DayWorkSchedule>>() {
                })
                .block();
    }


}
