package org.minjay.allitebooks;

import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.minjay.allitebooks.data.Book;
import org.minjay.allitebooks.data.BookRepository;
import org.minjay.allitebooks.data.Catalog;
import org.minjay.allitebooks.data.CatalogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class AllitebooksCapture {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final String URL = "http://www.allitebooks.com";
    public RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CatalogRepository catalogRepository;

    public Collection<String> getCatalog() throws ParserException {
        String indexResponse = restTemplate.getForObject(URL, String.class);
        Parser parser = Parser.createParser(indexResponse, "utf-8");
        Collection<String> result = new ArrayList<>();
        NodeList nodeList = parser.extractAllNodesThatMatch(node -> node.getText().contains("menu-categories"));
        int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            NodeList catalogList = nodeList.elementAt(i).getChildren();
            int catalogSize = catalogList.size();
            for (int j = 0; j < catalogSize; j++) {
                if (catalogList.elementAt(j).getChildren() == null) {
                    continue;
                }
                String text = catalogList.elementAt(j).getChildren().elementAt(0).getText();
                String catalogUrl = StringUtils.substringBetween(text, "=\"", "\"");
                System.out.println(catalogUrl);
                catalogRepository.save(new Catalog(catalogUrl));
            }

        }
        return result;
    }

    public void getBooks() throws Exception {
        Iterable<Catalog> catalogs = catalogRepository.findAll();
        for (Catalog catalog : catalogs) {
            String catalogResponse = restTemplate.getForObject(catalog.getName(), String.class);
            Parser parser = Parser.createParser(catalogResponse, "utf-8");
            int lastPage = Integer.valueOf(getLastPage(parser));
            LOG.info("开始抓取类别为:{} 的书籍名,一共 {} 页----------------------------------------------------------------", catalog.getName(), lastPage);
            for (int i = 1; i <= lastPage; i++) {
                LOG.info("开始抓取第 {} 页", i);
                if (i == 1) {
                    handleBookNameElement(catalogResponse);
                } else {
                    String pageResponse = restTemplate.getForObject(catalog.getName() + "page/" + i, String.class);
                    handleBookNameElement(pageResponse);
                }
            }
        }
    }

    private String getLastPage(Parser parser) throws Exception {
        NodeList nodeList = parser.extractAllNodesThatMatch(node -> node.getText().contains("Last Page"));
        String lastPage = StringUtils.substringBetween(nodeList.elementAt(0).getText(), "page/", "/");
        return lastPage;
    }

    private void handleBookNameElement(String response) throws Exception {
        Parser parser = Parser.createParser(response, "utf-8");
        NodeList nodeList = parser.extractAllNodesThatMatch(node -> node.getText().contains("bookmark"));
        int size = nodeList.size();
        for (int i = 0; i < size; i += 2) {
            String name = StringUtils.substringBetween(nodeList.elementAt(i).getText(), "=\"", "\"");
            if (!StringUtils.isEmpty(name)) {
                LOG.info("成功抓取到书链接: {}", name);
                bookRepository.save(new Book(name));
            }
        }
    }


}
