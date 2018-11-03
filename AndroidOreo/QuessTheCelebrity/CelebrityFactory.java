package com.samlinz.androidcourse.quesstheceleb;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class CelebrityFactory {
    private static final String LogName = Celebrity.class.getSimpleName();

    public static List<Celebrity> GetCelebritites(int count, String url) {
        try {
            final Document document = Jsoup.connect(url).get();
            final Elements celebEntries = document.getElementsByClass("channelListEntry");
            Log.i(LogName, String.format("Found %d entries", celebEntries.size()));

            int i = 0;

            List<Celebrity> result = new LinkedList<>();

            for (Element element : celebEntries) {

                String name = element.getElementsByClass("name")
                        .first()
                        .text();

                int rank = Integer.valueOf(element.getElementsByClass("position")
                        .first()
                        .text());

                String imageUrl =
                        element.getElementsByClass("image")
                                .first()
                                .getElementsByAttribute("src")
                                .attr("src");

                Log.i(LogName
                        , String.format("Found celebrity '%s', rank %d, image %s"
                        , name
                        , rank
                        , imageUrl));

                result.add(new Celebrity(name, imageUrl, rank));

                i++;
                if (i >= count) break;
            }

            return result;
        } catch (Exception e) {
            Log.e(LogName, "Failed to load celebrities", e);
            return null;
        }
    }
}
