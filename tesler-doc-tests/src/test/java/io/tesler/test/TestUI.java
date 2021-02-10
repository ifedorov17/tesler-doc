/*-
 * #%L
 * TESLERDOC - Tests
 * %%
 * Copyright (C) 2020 - 2021 Tesler Contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package io.tesler.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestUI {

	private SelenideElement LoginInput(int index) {
		return $(By.cssSelector(".ant-form-item-control input"), index);
	}

	private SelenideElement LoginButton = $(By.cssSelector(".ant-form-item-control button"));

	private SelenideElement Menu(String text) {
		return $(By.cssSelector("ul[class^='ant-menu ScreenNavigation__Container']"))
				.$$(By.cssSelector("li[class^='ant-menu-item ScreenNavigation__Item']")).findBy(
						Condition.exactText(text));
	}

	private SelenideElement FirstLevelMenu(String text) {
		return $(By.cssSelector("nav[class^='ViewNavigation__container']"))
				.$$(By.cssSelector("span[class^='ViewNavigation__item']")).findBy(Condition.exactText(text));
	}

	private SelenideElement SecondLevelMenu(String text) {
		return $(By.cssSelector("nav[class^='SecondLevelTabs__container']"))
				.$$(By.cssSelector("div[class^=' ant-tabs-tab']")).findBy(Condition.exactText(text));
	}

	private SelenideElement getInput(String fieldName, int index) {
		return $$(By.cssSelector(".ant-form-item")).filterBy(Condition.exactText(fieldName)).get(index)
				.$(By.tagName("input"));
	}

	private SelenideElement getInput(String fieldName) {
		return $$(By.cssSelector(".ant-form-item")).findBy(Condition.exactText(fieldName)).$(By.tagName("input"));
	}

	private SelenideElement getComboBox(String fieldName) {
		return $$(By.cssSelector(".ant-form-item")).findBy(Condition.text(fieldName));
	}

	private SelenideElement FirstRowTable() {
		return $(By.cssSelector("div[class^=TableWidget]")).$(By.cssSelector(".ant-table-tbody")).$$(By.tagName("tr"))
				.first();
	}

	private Integer getIndexColumnPickList(String columnName) {
		$(By.cssSelector("div[class^='ant-table-wrapper PickListPopup__table']")).shouldHave(Condition.visible);
		List<String> tableColumns = $(By.cssSelector("div[class^='ant-table-wrapper PickListPopup__table']"))
				.$$(By.tagName("th")).texts();
		return tableColumns.indexOf(columnName);
	}

	private Integer getIndexColumn(String columnName) {
		$(By.cssSelector("div[class^=TableWidget]")).shouldBe(Condition.visible);
		List<String> tableColumns = $(By.cssSelector("div[class^=TableWidget]")).$$(By.tagName("th")).texts();
		return tableColumns.indexOf(columnName);
	}

	private SelenideElement FindColumnInRow(String columnName, SelenideElement row) {
		$(By.cssSelector("div[class^='ant-table-wrapper PickListPopup__table']")).shouldHave(Condition.visible);
		return row.$$(By.tagName("td")).get(getIndexColumnPickList(columnName));
	}

	private SelenideElement FindRowPickListWithColumnValue(String columnName, String columnValue) {
		$(By.cssSelector("div[class^='ant-table-wrapper PickListPopup__table']")).shouldHave(Condition.visible);
		return $(By.cssSelector("div[class^='ant-table-wrapper PickListPopup__table']"))
				.$(By.cssSelector(".ant-table-tbody")).$$(By.tagName("tr"))
				.stream()
				.filter(r -> FindColumnInRow(columnName, r).getText().equals(columnValue))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("no row with columnValue found"));
	}

	private String LargeText = "Etiam nec eros nulla. Suspendisse viverra mollis dolor non iaculis. Duis imperdiet facilisis urna non ultrices. Mauris aliquet dui massa, sit amet iaculis enim blandit nec. Suspendisse tempus nunc imperdiet tellus faucibus feugiat.";

	private String SmallText = "Aliquam quis enim.";

	private DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	@Test
	public void TestUI() {

		Configuration.headless = true;
		Configuration.browserSize = "1440x768";

		Login();
		TestInput();
		TestDictionary();
		TestNumber();
		TestDate();
		TestPickList();
	}

	private void Login() {
		open("http://localhost:8080");

		LoginInput(0).setValue("Test");
		LoginButton.click();
		$(By.cssSelector(".ant-form-item-control >span"), 2).$(By.cssSelector("span[class^='Login__error']"))
				.shouldBe(Condition.visible);
		$(By.cssSelector(".ant-form-item-control >span"), 2).$(By.cssSelector("span[class^='Login__error']"))
				.shouldHave(Condition.exactText("Unauthorized"));
		LoginInput(0).setValue("vanilla");
		LoginButton.click();
	}

	private void TestInput() {
		//Navigate through the menu
		Menu("Components Overview").click();
		FirstLevelMenu("Fields").click();
		SecondLevelMenu("Input").click();

		//Checking the status of the Input field
		getInput("Test Input").shouldBe(Condition.readonly.negate());
		getInput("Test Input").shouldHave(Condition.attribute("placeholder", "PlaceholderInput test"));

		//Entering text in Input
		getInput("Test Input").setValue(LargeText);
		assertThat(getInput("Test Input").getAttribute("value")).isEqualTo(LargeText);

		//Moving the focus to another required field
		getInput("Name").setValue("Q");
		getInput("Name").sendKeys(Keys.BACK_SPACE);

		//Click "Сохранить"
		$$(By.cssSelector("div[class^='Operations__container'] button")).findBy(Condition.exactText("Сохранить")).click();

		//Checking the required field
		assertThat($(By.cssSelector(".ant-form-explain")).shouldBe(Condition.visible).getText())
				.isEqualTo("This field is mandatory");

		//Filling in the required field
		$(By.cssSelector(".ant-form-item-with-help input"))
				.setValue("This field is mandatory").pressTab();

		//Input and List Checks
		assertThat(getInput("Name").getAttribute("value")).isEqualTo("This field is mandatory");
		assertThat(getInput("Test Input").getAttribute("value")).isEqualTo(LargeText);
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).getText()).isEqualTo(LargeText);

		//Click "Сохранить"
		$$(By.cssSelector("div[class^='Operations__container'] button")).findBy(Condition.exactText("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Input").click();

		//Input and List Checks
		assertThat(getInput("Test Input").getAttribute("value")).isEqualTo(LargeText);
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).getText()).isEqualTo(LargeText);

		//Entering text into a cell on a List
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).doubleClick();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).$(By.tagName("input")).setValue(SmallText);

		//Saving with three dots
		actions().moveToElement(FirstRowTable()).perform();
		actions().moveToElement($(By.cssSelector("div[class^=TableWidget__dots]"))).perform();
		$(By.cssSelector("div[class^=TableWidget__dots]")).click();
		$(By.cssSelector("div[class^=ant-dropdown] ul")).$$(By.tagName("li")).find(Condition.text("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Input").click();

		//Input and List Checks
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).getText()).isEqualTo(SmallText);
		assertThat(getInput("Test Input").getAttribute("value")).isEqualTo(SmallText);

		//Clearing values on the List widget
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).doubleClick();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).$(By.tagName("input")).clear();

		//Moving the focus to another column in the table
		FirstRowTable().$$(By.tagName("td")).last().doubleClick();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Input").click();

		//Проверки значений в List-виджете и Input
		//Нет автосейва при измении фокуса?
		/*assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).getText()).isEmpty();
		assertThat(getInput("Test Input").getAttribute("value")).isEmpty();*/
	}

	private void TestDictionary() {
		SecondLevelMenu("Dictionary").click();

		//Selecting First value in the dropdown list on Form
		getComboBox("Dictionary").$(By.cssSelector(".ant-select-selection__rendered")).click();
		getComboBox("Dictionary").$(By.cssSelector("div[class^=ant-select-dropdown] ul")).shouldBe(Condition.visible);
		assertThat(getComboBox("Dictionary").$(By.cssSelector("div[class^=ant-select-dropdown] ul"))
				.$$(By.tagName("li")).size()).isEqualTo(4);
		getComboBox("Dictionary").$(By.cssSelector("div[class^=ant-select-dropdown] ul")).$$(By.tagName("li"))
				.first().click();

		//Click "Сохранить" on the Form
		getInput("Name").setValue("This field is mandatory");
		$$(By.cssSelector("div[class^='Operations__container'] button")).findBy(Condition.exactText("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Dictionary").click();

		//Dictionary and List checks
		assertThat(getComboBox("Dictionary").$(By.cssSelector(".ant-select-selection-selected-value"))
				.getAttribute("title")).isEqualTo("First value");
		assertThat($(By.cssSelector("div[class^=TableWidget]")).$(By.cssSelector(".ant-table-tbody")).$$(By.tagName("tr"))
				.first().$$(By.tagName("td")).get(getIndexColumn("Dictionary")).getText()).isEqualTo("First value");

		//Removing a selection from a dropdown list on Form
		actions().moveToElement(getComboBox("Dictionary").$(By.className("ant-select-selection__clear"))).perform();
		getComboBox("Dictionary").$(By.className("ant-select-selection__clear")).shouldBe(Condition.visible).click();

		//Click "Сохранить" on the Form
		$$(By.cssSelector("div[class^='Operations__container'] button")).findBy(Condition.exactText("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Dictionary").click();

		//Dictionary and List checks
		assertThat(getComboBox("Dictionary").$(By.cssSelector(".ant-select-selection-selected-value"))
				.getAttribute("title")).isEmpty();
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Dictionary")).getText()).isEmpty();

		//Selecting the Fourth value in the dropdown list on List
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Dictionary")).doubleClick();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Dictionary"))
				.$(By.cssSelector(".ant-select-selection--single")).click();
		assertThat($(By.cssSelector("div[class^=ant-select-dropdown] ul")).$$(By.tagName("li")).size()).isEqualTo(4);
		$(By.cssSelector("div[class^=ant-select-dropdown] ul")).$$(By.tagName("li")).last().click();

		//Saving with three dots
		actions().moveToElement(FirstRowTable()).perform();
		actions().moveToElement($(By.cssSelector("div[class^=TableWidget__dots]"))).perform();
		$(By.cssSelector("div[class^=TableWidget__dots]")).click();
		$(By.cssSelector("div[class^=ant-dropdown] ul")).$$(By.tagName("li")).find(Condition.text("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Dictionary").click();

		//Dictionary and List checks
		assertThat(getComboBox("Dictionary").$(By.cssSelector(".ant-select-selection-selected-value"))
				.getAttribute("title")).isEqualTo("Fourth value");
		assertThat($(By.cssSelector("div[class^=TableWidget]")).$(By.cssSelector(".ant-table-tbody")).$$(By.tagName("tr"))
				.first().$$(By.tagName("td")).get(getIndexColumn("Dictionary")).getText()).isEqualTo("Fourth value");

		//Clear a Dictionary on a List
		FirstRowTable().$$(By.tagName("td")).last().scrollIntoView(true);
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Dictionary")).doubleClick();
		actions().moveToElement(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Dictionary"))
				.$(By.className("ant-select-selection__clear"))).perform();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Dictionary"))
				.$(By.className("ant-select-selection__clear")).shouldBe(Condition.visible).click();

		//Moving the focus to another column in the table
		FirstRowTable().$$(By.tagName("td")).last().doubleClick();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Dictionary").click();

		//Автосейв при смене фокуса на другую колонку?
		//Проверки Dictionary и List
		/*assertThat(getComboBox("Dictionary").$(By.cssSelector(".ant-select-selection-selected-value"))
				.getAttribute("title")).isEmpty();
		assertThat($(By.cssSelector("div[class^=TableWidget]")).$(By.cssSelector(".ant-table-tbody")).$$(By.tagName("tr"))
				.first().$$(By.tagName("td")).get(getIndexColumn("Dictionary")).getText()).isEmpty();*/
	}

	private void TestNumber() {
		SecondLevelMenu("Number").click();

		//Entering text values in fields on Form
		getInput("Number").setValue("Text");
		getInput("Money").setValue("Text");
		getInput("Percent").setValue("Text");
		getInput("Percent").pressTab();

		//Field checks
		assertThat(getInput("Number").getAttribute("value")).isEqualTo("0");
		assertThat(getInput("Money").getAttribute("value")).isEqualTo("0,00");
		assertThat(getInput("Percent").getAttribute("value")).isEqualTo("0 %");

		//Entering numeric values in fields on Form
		getInput("Number").setValue("-10g000");
		getInput("Money").setValue("01222333.489");
		getInput("Percent").setValue("-998,500");

		//Click "Сохранить" on the Form
		$$(By.cssSelector("div[class^='Operations__container'] button")).findBy(Condition.exactText("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Number").click();

		//Numbers and List checks
		assertThat(getInput("Number").getAttribute("value")).isEqualTo("-10" + "\u00A0" + "000");
		assertThat(getInput("Money").getAttribute("value")).isEqualTo("1" + "\u00A0" + "222" + "\u00A0" + "333,49");
		//TODO с активным фокусом на поле не происходит округления
		assertThat(getInput("Percent").getAttribute("value")).isEqualTo("-998 %");

		//TODO неверное отображение в List виджете (в режиме просмотра нет сотых долей, знака %)
		/*assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Number")).getText()).isEqualTo("-10"+"\u00A0"+"000");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Money")).getText()).isEqualTo("1"+"\u00A0"+"222"+"\u00A0"+"333,49");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Percent")).getText()).isEqualTo("-998 %");*/
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Number")).getText()).isEqualTo("-10 000");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Money")).getText()).isEqualTo("1 222 333");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Percent")).getText()).isEqualTo("-998");

		//Entering values on List
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Number")).doubleClick();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Number")).$(By.tagName("input")).clear();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Number")).$(By.tagName("input")).setValue("g");
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Number")).$(By.tagName("input")).setValue("-20000,1");
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Money")).doubleClick();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Money")).$(By.tagName("input")).clear();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Money")).$(By.tagName("input")).setValue("g");
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Money")).$(By.tagName("input")).setValue("-7000,126");
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Percent")).doubleClick();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Percent")).$(By.tagName("input")).clear();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Percent")).$(By.tagName("input")).setValue("g");
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Percent")).$(By.tagName("input")).setValue("0800,512");

		//Saving with three dots
		actions().moveToElement(FirstRowTable()).perform();
		actions().moveToElement($(By.cssSelector("div[class^=TableWidget__dots]"))).perform();
		$(By.cssSelector("div[class^=TableWidget__dots]")).click();
		$(By.cssSelector("div[class^=ant-dropdown] ul")).$$(By.tagName("li")).find(Condition.text("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Number").click();

		//Numbers and List checks
		assertThat(getInput("Number").getAttribute("value")).isEqualTo("-20" + "\u00A0" + "000");
		assertThat(getInput("Money").getAttribute("value")).isEqualTo("-7" + "\u00A0" + "000,13");
		//TODO с активным фокусом на поле не происходит округления
		assertThat(getInput("Percent").getAttribute("value")).isEqualTo("800 %");

		//TODO неверное отображение в List виджете
		/*assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Number")).getText()).isEqualTo("-20"+"\u00A0"+"000");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Money")).getText()).isEqualTo("-7"+"\u00A0"+"000,13");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Percent")).getText()).isEqualTo("801"+"\u00A0"+"%");*/
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Number")).getText()).isEqualTo("-20 000");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Money")).getText()).isEqualTo("-7 000");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Percent")).getText()).isEqualTo("800");
	}

	private void TestDate() {
		//SecondLevelMenu("Date").click();
		Menu("Components Overview").click();
		FirstLevelMenu("Fields").click();
		SecondLevelMenu("Date").click();

		//Checking that the date is not filled in
		if (FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).getText().isEmpty()) {
			return;
		} else {
			FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).doubleClick();
			actions().moveToElement(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date"))
					.$(By.className("ant-calendar-picker-clear"))).perform();
			FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).$(By.className("ant-calendar-picker-clear"))
					.shouldBe(Condition.visible).click();
		}

		//Selecting the current day on Form
		getInput("Date").click();
		$(By.cssSelector(".ant-calendar-table")).shouldBe(Condition.visible);
		$(By.cssSelector(".ant-calendar-tbody"))
				.$$(By.tagName("tr")).findBy(Condition.cssClass("ant-calendar-current-week"))
				.$$(By.tagName("td"))
				.findBy(Condition.cssClass("ant-calendar-today"))
				.click();
		LocalDateTime date = LocalDateTime.now();

		//Click "Сохранить" on the Form
		$$(By.cssSelector("div[class^='Operations__container'] button")).findBy(Condition.exactText("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Date").click();

		//Checking the date completion
		assertThat(getInput("Date").getAttribute("value")).isEqualTo(LocalDate.now().format(DATE_FORMATTER));
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).getText())
				.isEqualTo(date.format(DATE_FORMATTER));
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date and Time")).getText())
				.isEqualTo(date.format(DATE_TIME_FORMATTER));

		//Selecting the current day on List
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).doubleClick();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).click();
		$(By.cssSelector(".ant-calendar-table")).shouldBe(Condition.visible);
		$(By.cssSelector(".ant-calendar-footer a")).click();

		//Checking the date completion
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).$(By.cssSelector("input")).getValue())
				.isEqualTo(date.format(DATE_FORMATTER));

		//Clear Date picker on List
		actions().moveToElement(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date"))
				.$(By.className("ant-calendar-picker-clear"))).perform();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).$(By.className("ant-calendar-picker-clear"))
				.shouldBe(Condition.visible).click();

		//Saving with three dots
		actions().moveToElement(FirstRowTable()).perform();
		actions().moveToElement($(By.cssSelector("div[class^=TableWidget__dots]"))).perform();
		$(By.cssSelector("div[class^=TableWidget__dots]")).click();
		$(By.cssSelector("div[class^=ant-dropdown] ul")).$$(By.tagName("li")).find(Condition.text("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Date").click();

		//Check Date is empty
		assertThat(getInput("Date").getAttribute("value")).isEmpty();
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date")).getText()).isEmpty();
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Date and Time")).getText()).isEmpty();
	}

	private void TestPickList() {
		SecondLevelMenu("Picklist").click();

		//Clearing the input field
		getInput("Test Input").clear();
		assertThat(getInput("Test Input").getValue()).isEmpty();

		//Calling Popup on Form
		$$(By.cssSelector(".ant-form-item")).findBy(Condition.exactText("Pick List"))
				.$(By.cssSelector("i[class='anticon anticon-paper-clip']")).click();

		//Click on a column in Popup
		FindColumnInRow("Name", FindRowPickListWithColumnValue("Name", "This field is mandatory")).click();

		//Click "Сохранить" on the Form
		$$(By.cssSelector("div[class^='Operations__container'] button")).findBy(Condition.exactText("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Picklist").click();

		//Checking the selected values
		assertThat(getInput("Test input").getValue()).isEqualTo("-20000");
		assertThat(getInput("Pick List").getValue()).isEqualTo("This field is mandatory");
		getInput("Pick List").shouldBe(Condition.readonly);
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).getText()).isEqualTo("-20000");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Pick List")).getText())
				.isEqualTo("This field is mandatory");

		//Clearing the input field
		getInput("Test Input").clear();
		assertThat(getInput("Test Input").getValue()).isEmpty();

		//Calling Popup and selecting values on List
		FirstRowTable().$$(By.tagName("td")).last().scrollIntoView(true);
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Pick List")).doubleClick();
		FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Pick List"))
				.$(By.cssSelector("i[class='anticon anticon-paper-clip']")).click();
		FindColumnInRow("Name", FindRowPickListWithColumnValue("Name", "This field is mandatory")).click();

		//Saving with three dots
		FirstRowTable().$$(By.tagName("td")).last().scrollIntoView(true);
		actions().moveToElement(FirstRowTable()).perform();
		actions().moveToElement($(By.cssSelector("div[class^=TableWidget__dots]"))).perform();
		$(By.cssSelector("div[class^=TableWidget__dots]")).click();
		$(By.cssSelector("div[class^=ant-dropdown] ul")).$$(By.tagName("li")).find(Condition.text("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Picklist").click();

		//Checking the selected values
		assertThat(getInput("Test input").getValue()).isEqualTo("-20000");
		assertThat(getInput("Pick List").getValue()).isEqualTo("This field is mandatory");
		getInput("Pick List").shouldBe(Condition.readonly);
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).getText()).isEqualTo("-20000");
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Pick List")).getText())
				.isEqualTo("This field is mandatory");

		//Clearing the PickList value on Form
		$$(By.cssSelector(".ant-form-item")).findBy(Condition.exactText("Pick List")).$(By.className("ant-input-suffix"))
				.shouldHave(Condition.visible);
		$$(By.cssSelector(".ant-form-item")).findBy(Condition.exactText("Pick List")).$(By.className("ant-input-suffix"))
				.click();

		//Click "Сохранить" on the Form
		$$(By.cssSelector("div[class^='Operations__container'] button")).findBy(Condition.exactText("Сохранить")).click();

		//Navigating tabs
		SecondLevelMenu("Fields").click();
		SecondLevelMenu("Picklist").click();

		//Check PickList value is empty
		assertThat(getInput("Test input").getValue()).isEmpty();
		assertThat(getInput("Pick List").getValue()).isEmpty();
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Test Input")).getText()).isEmpty();
		assertThat(FirstRowTable().$$(By.tagName("td")).get(getIndexColumn("Pick List")).getText()).isEmpty();
	}

}