import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class WebScraper {
    private static final int MAX_ARTICLES = 5;
    private static final String BASE_URL = "https://www.eurekaselect.com/search?searchvalue=";
    private static final String NEXT_PAGE_XPATH = ".//a[contains(@class, 'btn-primary') and text()='Next']";
    private static final String ARTICLE_LINK_XPATH = "//h4/a";

    public static void main(String[] args) {
        try {
            String searchQuery = "co2";  // Replace with user input
            List<Book> books = new ArrayList<>();
            int articleCount = 0;
            String url = BASE_URL + searchQuery;

        System.out.println("Starting web scraping...");

            // Loop through pages
            while (articleCount < MAX_ARTICLES) {
                //////////System.out.println("Fetching page: " + url);

                Document doc = Jsoup.connect(url).get();


                // Check if the document was retrieved
                if (doc == null) {
                    ////////System.out.println("Failed to retrieve document.");
                    break;
                }

                Elements articleLinks = doc.selectXpath(ARTICLE_LINK_XPATH);


              //  ////////System.out.println("Found " + articleLinks.size() + " article links on current page.");

                // Collect article URLs and fetch each article's details
                for (Element linkElement : articleLinks) {
                    String articleUrl = linkElement.absUrl("href");
                   // ////////System.out.println("Fetching article: " + articleUrl);

                    if (!articleUrl.contains("article"))
                        continue;
                    Book book = scrapeArticle(articleUrl);
                    if (book != null) {
                        book.setArticle_link(articleUrl); // Save the link of the article
                        books.add(book);
                        articleCount++;
                    } else {
                      //  ////////System.out.println("Failed to scrape article: " + articleUrl);
                    }

                    if (articleCount >= MAX_ARTICLES) break;
                }

                // Find next page link
                Element nextPageElement = doc.selectXpath(NEXT_PAGE_XPATH).first();
                if (nextPageElement == null || articleCount >= MAX_ARTICLES) {
                  //  ////////System.out.println("No more pages or reached max articles.");
                    break;
                }
                url = nextPageElement.absUrl("href");
            }

            // Export to CSV
            exportToCSV(books, "articles.csv");
        //    ////////System.out.println("Scraping completed. Data saved to articles.csv.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Book scrapeArticle(String articleUrl) {
        try {

            Document doc = Jsoup.connect(articleUrl).get();

            // Create a new Book object
            Book book = new Book();

            // Extract title
            Element titleElement = doc.selectXpath("//h1").first();
            String title = titleElement != null ? titleElement.text() : "No Title";
            book.setTitle(title);


          //  ////////System.out.println("Title: " + title);

            // Extract authors
            Elements authorElements = doc.selectXpath("//div[@id='side-b']//span[@class='l-h-3']");
            String authors = authorElements.stream().map(Element::text).reduce((a, b) -> a + ", " + b).orElse("No Authors");
            book.setAuthor(authors);

//          //  ////////System.out.println("Authors: " + authors);
//            Elements CitationsElements = doc.selectXpath("//div[contains(@class, 'fixpadt--l line-separator')]/div[@class='c__14']/text()");
//            String Citations = CitationsElements.stream().map(Element::text).reduce((a, b) -> a + ", " + b).orElse("No Citations");
//            book.setCitations(Citations);
//            // Extract publish date

            Element publishDateElement = doc.selectXpath("//strong[contains(text(), 'Published on:')]/following-sibling::text()[1]").first();
            String publishDate = publishDateElement != null ? publishDateElement.text() : "No Publish Date";
            book.setPublishDate(publishDate);
            ////////System.out.println("Publish Date: " + publishDate);

//            Element abstractElement = doc.selectXpath("//div[contains(@class, 'capsule__column-wrapper')]//div[contains(@class, 'capsule__text')]/p").first();
//            String Abstract = abstractElement != null ? publishDateElement.text() : "No Publish Date";
            String Abstract = extractAbstract(doc,book);
            book.setAbstract(Abstract);
            // Extract number of pages
            Element pageCountElement = doc.selectXpath("//div[@class='col-md-8']/p[3][contains(text(), '')]/text()[last()]").first();
            String pageCount = pageCountElement != null ? pageCountElement.text() : "No Page Count";
            book.setPages(pageCount);

            // Extract DOI
            Element doiElement = doc.selectXpath("//div[@class='col-md-8']/p[contains(., 'DOI')]/a/@href").last();
            String doi = doiElement != null ? doiElement.text() : "No DOI";
            if (doi == "No DOI"){
                doc.cssSelector().contains("#side-b > div > div > p:nth-child(3) > a");
            }
            book.setDOI(doi);



            return book;

        } catch (IOException e) {
            ////////System.out.println("Error fetching article at: " + articleUrl);
            e.printStackTrace();
            return null;
        }
    }
    public static String extractAbstract(Document htmlDocument, Book book) {
        try {
            // Select all paragraph nodes within the card body of the abstract section
            Elements selectedNodes = htmlDocument.select("div#abstract div.card-body p");

            // Initialize a StringBuilder to concatenate all abstract text
            StringBuilder abstractBuilder = new StringBuilder();

            if (selectedNodes != null && !selectedNodes.isEmpty()) {
                // Append each paragraph's text content
                for (Element node : selectedNodes) {
                    abstractBuilder.append(node.text().trim()).append("\n");
                }

                // Select any additional text that is not within <p> tags
                Element additionalTextNode = htmlDocument.selectFirst("div#abstract div.card-body");
                if (additionalTextNode != null) {
                    // Remove newline characters, replace with space, and trim
                    String additionalText = additionalTextNode.text().replace("\n", " ").replace("\r", " ").trim();

                    // Only add if it's not empty
                    if (!additionalText.isEmpty()) {
                        abstractBuilder.append(additionalText).append("\n");
                    }
                }

                // Set the abstract text to the Book object
//                book.setAbstract(abstractBuilder.toString().trim());
              return   abstractBuilder.toString().trim();
            } else {
                // Handle the case where no paragraphs were found
                return   "No abstract available.";
            }
        } catch (Exception e) {
            // Log the exception or handle it accordingly
            System.out.println("An error occurred: " + e.getMessage());
            return "An error occurred:";
        }

    }
    public static void exportToCSV(List<Book> books, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Writing the CSV header
            writer.append("Title,Authors,Publish Date,Pages,DOI,Abstract,Link\n");

            // Writing each book's details
            for (Book book : books) {
                writer.append(escapeSpecialCharacters(book.getTitle())).append(",");
                writer.append(escapeSpecialCharacters(book.getAuthor())).append(",");
                writer.append(escapeSpecialCharacters(book.getPublishDate())).append(",");
                writer.append(String.valueOf(book.getPages())).append(",");
                writer.append(escapeSpecialCharacters(book.getDOI())).append(",");
                writer.append(escapeSpecialCharacters(book.getAbstract())).append(",");
                writer.append(escapeSpecialCharacters(book.getArticle_link())).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to escape special characters for CSV format
    private static String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replace("\"", "\"\"");  // Escape double quotes
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            escapedData = "\"" + escapedData + "\"";  // Enclose field in quotes
        }
        return escapedData;
    }

}
