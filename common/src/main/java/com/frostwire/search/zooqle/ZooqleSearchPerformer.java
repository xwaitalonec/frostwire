/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2017, FrostWire(R). All rights reserved.
 *
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
 */

package com.frostwire.search.zooqle;

import com.frostwire.search.CrawlableSearchResult;
import com.frostwire.search.SearchMatcher;
import com.frostwire.search.torrent.TorrentRegexSearchPerformer;

/**
 * @author aldenml
 * @author gubatron
 */
public final class ZooqleSearchPerformer extends TorrentRegexSearchPerformer<ZooqleSearchResult> {

    private static final int MAX_RESULTS = 30;
    private static final String PRELIMINARY_RESULTS_REGEX =
            "(?is)<i class=\".*?text-muted2 zqf-small pad-r2\"></i><a class=\".*?small\"href=\"/(?<detailPath>.*?).html\">.*?</a>";
    private static final String HTML_DETAIL_REGEX = "(?is)<h4 id=torname>(?<filename>.*?)<span.*?" +
            "Seeders: (?<seeds>\\d*).*?" +
            "title=\"File size\"></i>(?<size>[\\d\\.\\,]*) (?<sizeUnit>.*?)<span class.*?" +
            "title=\"Date indexed\"></i>(?<month>.*?) (?<day>[\\d]*), (?<year>[\\d]*) <span.*?" +
            "urn:btih:(?<infohash>.*?)&.*?" +
            "href=\"/download/(?<torrent>.*?)\\.torrent\"";

    public ZooqleSearchPerformer(String domainName, long token, String keywords, int timeout) {
        super(domainName, token, keywords, timeout, 1, 2 * MAX_RESULTS, MAX_RESULTS, PRELIMINARY_RESULTS_REGEX, HTML_DETAIL_REGEX);
    }

    @Override
    public CrawlableSearchResult fromMatcher(SearchMatcher matcher) {
        return new ZooqleTempSearchResult(getDomainName(), matcher.group("detailPath") + ".html");
    }

    @Override
    protected String getUrl(int page, String encodedKeywords) {
        return "https://" + getDomainName() + "/search?pg=" + page + "&q=" + encodedKeywords + "&s=ns&v=t&sd=d";
    }

    @Override
    protected ZooqleSearchResult fromHtmlMatcher(CrawlableSearchResult sr, SearchMatcher matcher) {
        return new ZooqleSearchResult(sr.getDetailsUrl(), "https://" + getDomainName(), matcher);
    }

    @Override
    protected int preliminaryHtmlPrefixOffset(String page) {
        return page.indexOf("<i class=\"spr feed\"></i>");
    }

    @Override
    protected int preliminaryHtmlSuffixOffset(String page) {
        return page.indexOf("<ul class=\"pagination");
    }

    @Override
    protected int htmlPrefixOffset(String html) {
        int offset = html.indexOf("id=torrent><div class=panel-body");
        if (offset == -1) {
            return super.htmlPrefixOffset(html);
        }
        return offset;
    }

    @Override
    protected int htmlSuffixOffset(String html) {
        int offset = html.indexOf("Contact & Info");
        if (offset == -1) {
            return super.htmlSuffixOffset(html);
        }
        return offset;
    }

    /*
     public static void main(String[] args) throws Throwable {
     byte[] preliminaryResultsBytes = Files.readAllBytes(Paths.get("/Users/gubatron/Desktop/zooqle_preliminary.html"));
     String preliminaryResultsString = new String(preliminaryResultsBytes,"utf-8");
     Pattern preliminaryResultsPattern = Pattern.compile(PRELIMINARY_RESULTS_REGEX);
     Matcher preliminaryMatcher = preliminaryResultsPattern.matcher(preliminaryResultsString);
     int found = 0;
     while (preliminaryMatcher.find()) {
     found++;
     LOG.info("found " + found);
     LOG.info(preliminaryMatcher.group("detailPath")+".html");
     }


     byte[] htmlDetailsBytes = Files.readAllBytes(Paths.get("/Users/gubatron/Desktop/zooqle_detail.html"));
     String htmlDetailsString = new String(htmlDetailsBytes, "utf-8");
     Pattern htmlDetailPattern = Pattern.compile(HTML_DETAIL_REGEX);
     Matcher detailMatcher = htmlDetailPattern.matcher(htmlDetailsString);

     while (detailMatcher.find()) {
     System.out.println("filename: [" + detailMatcher.group("filename") + "]");
     System.out.println("seeds: [" + detailMatcher.group("seeds") + "]");
     System.out.println("infohash: [" + detailMatcher.group("infohash") + "]");
     System.out.println("torrent: [" + detailMatcher.group("torrent") + "]");
     System.out.println("size: [" + detailMatcher.group("size") + "]");
     System.out.println("sizeUnit: [" + detailMatcher.group("sizeUnit") + "]");
     System.out.print("creationtime: [");


     SearchMatcher sm = new SearchMatcher(detailMatcher);
     ZooqleSearchResult sr = new ZooqleSearchResult("https://zooqle.com/blabla.html", "http://zooqle.com", sm);
     System.out.println(sr.getCreationTime() + "]");
     System.out.println("size in bytes: [" + sr.getSize() + "]");
     System.out.println("===");
     }
     System.out.println("-done-");
     }
     */
}
