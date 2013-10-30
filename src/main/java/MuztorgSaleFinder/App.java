package MuztorgSaleFinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        MuztorgParser muztorgParser = new MuztorgParser();
        for (Item item : muztorgParser.parse()) {
            System.out.println(item);
        }
    }

    public static class MuztorgParser {
        public Collection<Item> parse() throws IOException {
            return getItems(getElement());
        }

        public Document getDocument() throws IOException {
            return Jsoup.connect("http://www.muztorg.ru/cat/1082/153283/153312/all/").timeout(0).get();
        }

        public Elements getElement() throws IOException {
            Elements result = getDocument().select("div.tovar");
            return result;
        }

        public Collection<Item> getItems(Elements elements) {
            List<Item> result = new LinkedList<>();
            for (Element element : elements) {
                Element priceElement = element.select("div.r4").select("span.p").first();
                Item item = new Item();
                item.setName(element.select("div.r2").select("a").first().text());
                if (priceElement.select("small").first() == null) {
                    continue;
                }
                Integer oldPrice = parsePrice(priceElement.select("small").first().text());
                item.setPrice(parsePrice(priceElement.select("span").get(3).text()));
                item.setDiff(oldPrice - item.getPrice());
                item.setPercent((item.getDiff() * 1.0) / oldPrice);
                result.add(item);
            }

            Collections.sort(result, new ItemComperator());

            return result;
        }

        public Integer parsePrice(String priceString) {
            return Integer.valueOf(priceString.replace("руб.", "").replace(" ", ""));
        }
    }

    public static class ItemComperator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getPercent().compareTo(o2.getPercent());
        }
    }

    public static class Item {
        private Integer price;
        private Integer diff;
        private String name;
        private Double percent;

        @Override
        public String toString() {
            return "Item{" +
                    "price=" + price +
                    ", diff=" + diff +
                    ", name='" + name + '\'' +
                    ", percent=" + percent +
                    '}';
        }

        public Double getPercent() {
            return percent;
        }

        public void setPercent(Double percent) {
            this.percent = percent;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Integer getDiff() {
            return diff;
        }

        public void setDiff(Integer diff) {
            this.diff = diff;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
