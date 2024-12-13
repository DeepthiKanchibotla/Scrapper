package demo.wrappers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.Assert;

import java.io.File;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */

    public static void navigateURL(ChromeDriver driver, String url) {
        try {
            driver.get(url);
            String currentURL = driver.getCurrentUrl();
            if (currentURL.contains("scrape")) {
                System.out.println("Navigated to correct URL");
            } else {
                System.out.println("Navigated to wrong URL");
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void navigateToPage(ChromeDriver driver, String page) {
        try {
            WebElement pageLink = driver.findElement(By.xpath("//a[contains(text(),'" + page + "')]"));
            pageLink.click();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1")));
            WebElement header = driver.findElement(By.xpath("//h1[contains(text(),'" + page + "')]"));

            if (header.isDisplayed()) {
                System.out.println("Navigate to URL");
            } else {
                System.out.println("Navigate to worng URL");
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void teamData(ChromeDriver driver) {
        try {

            ArrayList<HashMap<String, Object>> teamData = new ArrayList<>();

            for (int i = 0; i < 4; i++) {

                List<WebElement> allWins = driver.findElements(By.xpath("//td[contains(@class,'pct')]"));
                for (WebElement eachWin : allWins) {
                    String percentageText = eachWin.getText().trim();
                    Double percentagevalue = Double.parseDouble(percentageText);
                    if (percentagevalue < 0.40) {
                        String teamName = driver.findElement(By.xpath("//td[@class='name']")).getText();
                        String year = driver.findElement(By.xpath("//td[@class='year']")).getText().trim();

                        HashMap<String, Object> eachTeamData = new HashMap<>();

                        eachTeamData.put("epochTime", Instant.now().getEpochSecond());
                        eachTeamData.put("teamName", teamName);
                        eachTeamData.put("Year", year);
                        eachTeamData.put("Win %", percentagevalue);

                        teamData.add(eachTeamData);

                    }

                }

                if (i < 4) {
                    WebElement pageNextIcon = driver.findElement(By.xpath("//a[@aria-label='Next']/span"));
                    pageNextIcon.click();
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            String fileName = "src/test/resources/hockey-team-data.json";
            mapper.writeValue(new File(fileName), teamData);

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void bestPictureWinner(ChromeDriver driver) {
        try {
            ArrayList<HashMap<String, Object>> movieData = new ArrayList<>();

            List<WebElement> allLinks = driver.findElements(By.xpath("//a[@class='year-link']"));
            for (WebElement eachlink : allLinks) {
                String year = eachlink.getText();
                eachlink.click();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//th[text()='Title']")));
                List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='film']"));
                for (int i = 0; i < Math.min(5, rows.size()); i++) {
                    WebElement row = rows.get(i);
                    String title = row.findElement(By.xpath(".//td[@class='film-title']")).getText();
                    System.out.println(title);
                    String nominations = row.findElement(By.xpath(".//td[@class='film-nominations']")).getText();
                    System.out.println(nominations);
                    String awards = row.findElement(By.xpath(".//td[@class='film-awards']")).getText();
                    System.out.println(awards);
                    boolean isWinner = row
                            .findElements(
                                    By.xpath(".//td[@class='film-best-picture']/i[contains(@class, 'glyphicon-flag')]"))
                            .isEmpty();
                    boolean isWinnerStatus = !isWinner;
                    System.out.println(isWinnerStatus);

                    HashMap<String, Object> movie = new HashMap<>();
                    movie.put("epochTime", Instant.now().getEpochSecond());
                    movie.put("year", year);
                    movie.put("title", title);
                    movie.put("nominations", nominations);
                    movie.put("awards", awards);
                    movie.put("isWinner", isWinnerStatus);

                    movieData.add(movie);
                }

                driver.navigate().back();

            }

            ObjectMapper mapper = new ObjectMapper();
            String filePath = "src/test/resources/oscar-winner-data.json";
            File outputFile = new File(filePath);
            outputFile.getParentFile().mkdirs(); // Create directories if not exist
            mapper.writeValue(outputFile, movieData);

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void verifyFile() {
        String filePath = "src/test/resources/oscar-winner-data.json";
        File file = new File(filePath);
        Assert.assertTrue(file.exists(), "File does not exist");
        Assert.assertTrue(file.length() > 0, "File is empty");

    }
}
