package ru.netology.web;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class DeliveryTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Корректные данные
    private final String city = "Севастополь";
    private final LocalDate date = LocalDate.now().plusDays(5);
    private final String name = "Иванов Иван";
    private final String phone = "+79156757490";

    /**
     * Положительный сценарий:
     * отправка полностью заполненной формы с корректными значениями
     * */
    @Test
    void shouldSubmitWithValidData() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue(city);
        $("[data-test-id=date] input").sendKeys(formatter.format(date));
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();

        // Assertion: нужно ок. 15 с,
        // чтобы появился div c сообщением об успешной заявке,
        // поэтому нужен таймаут
        Duration timeout = Duration.ofSeconds(15);
        $(withText("Успешно!")).shouldBe(visible, timeout);
    }

    /**
     * Негативный сценарий: некорректный город
     * */
    @Test
    void shouldSubmitWithIncorrectCity() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("SSSS");
        $("[data-test-id=date] input").sendKeys(formatter.format(date));
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();

        //Assertion: появляется сообщение об ошибке
        $("[data-test-id=city] .input__sub").shouldBe(exist);
    }

    /**
     * Негативный сценарий: некорректная дата доставки (5 дней назад)
     * */
    @Test
    void shouldSubmitWithIncorrectDate() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue(city);
        LocalDate pastDate = LocalDate.now().minusDays(5);
        $("[data-test-id=date] input").doubleClick().sendKeys(formatter.format(pastDate));
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();

        //Assertion: появляется сообщение об ошибке
        $("[data-test-id=date] .input__sub").shouldBe(exist);
    }

    /**
     * Негативный сценарий: некорректные имя с фамилией
     * */
    @Test
    void shouldSubmitWithIncorrectName() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue(city);
        $("[data-test-id=date] input").sendKeys(formatter.format(date));

        $("[data-test-id=name] input").setValue("Ivanov Ivan");
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();

        // Assertion: появляется сообщение об ошибке
        $("[data-test-id=name] .input__sub").shouldBe(exist);
    }

    /**
     * Негативный сценарий: некорректный номер телефона
     * */
    @Test
    void shouldSubmitWithIncorrectPhone() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue(city);
        $("[data-test-id=date] input").sendKeys(formatter.format(date));
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue("6757645");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        //Assertion: появляется сообщение об ошибке
        $("[data-test-id=phone] .input__sub").shouldBe(exist);
    }

    /**
     * Негативный сценарий: без чекбокса
     * */
    @Test
    void shouldSubmitWithoutAgreement() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue(city);
        $("[data-test-id=date] input").sendKeys(formatter.format(date));
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $(".button").click();

        //Assertion: у span появляется класс input_invalid, =>, текст у чекбокса становится красным
        $(".input_invalid").shouldBe(exist);
    }

}
