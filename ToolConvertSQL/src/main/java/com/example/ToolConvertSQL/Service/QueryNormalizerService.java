package com.example.ToolConvertSQL.Service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class QueryNormalizerService {

    private static final Map<String, String> REPLACEMENTS =
            new LinkedHashMap<>();

    static {

        // ==================================================
        // COUNTRIES
        // ==================================================

        REPLACEMENTS.put("người pháp", "France");
        REPLACEMENTS.put("pháp", "France");

        REPLACEMENTS.put("người mỹ", "USA");
        REPLACEMENTS.put("hoa kỳ", "USA");
        REPLACEMENTS.put("mỹ", "USA");

        REPLACEMENTS.put("người nhật", "Japan");
        REPLACEMENTS.put("nhật bản", "Japan");
        REPLACEMENTS.put("nhật", "Japan");

        REPLACEMENTS.put("người hàn", "South Korea");
        REPLACEMENTS.put("hàn quốc", "South Korea");

        REPLACEMENTS.put("người trung", "China");
        REPLACEMENTS.put("trung quốc", "China");

        REPLACEMENTS.put("người anh", "United Kingdom");
        REPLACEMENTS.put("vương quốc anh", "United Kingdom");
        REPLACEMENTS.put("anh", "United Kingdom");

        REPLACEMENTS.put("canada", "Canada");

        REPLACEMENTS.put("hồng kông", "Hong Kong");
        REPLACEMENTS.put("đài loan", "Taiwan");

        // ==================================================
        // LANGUAGES
        // ==================================================

        REPLACEMENTS.put("tiếng anh", "English");
        REPLACEMENTS.put("anh ngữ", "English");

        REPLACEMENTS.put("tiếng pháp", "French");
        REPLACEMENTS.put("pháp ngữ", "French");

        REPLACEMENTS.put("tiếng nhật", "Japanese");
        REPLACEMENTS.put("nhật ngữ", "Japanese");

        REPLACEMENTS.put("tiếng hàn", "Korean");
        REPLACEMENTS.put("hàn ngữ", "Korean");

        REPLACEMENTS.put("tiếng trung", "Chinese");
        REPLACEMENTS.put("trung ngữ", "Chinese");

        REPLACEMENTS.put("tiếng bồ đào nha", "Portuguese");

        // ==================================================
        // GENRES
        // ==================================================

        REPLACEMENTS.put("hành động", "Action");
        REPLACEMENTS.put("chính kịch", "Drama");
        REPLACEMENTS.put("kịch tính", "Drama");

        REPLACEMENTS.put("khoa học viễn tưởng", "Sci-Fi");
        REPLACEMENTS.put("viễn tưởng", "Sci-Fi");

        REPLACEMENTS.put("giật gân", "Thriller");

        REPLACEMENTS.put("hài", "Comedy");
        REPLACEMENTS.put("hài hước", "Comedy");

        REPLACEMENTS.put("tình cảm", "Romance");
        REPLACEMENTS.put("lãng mạn", "Romance");

        REPLACEMENTS.put("fantasy", "Fantasy");
        REPLACEMENTS.put("kỳ ảo", "Fantasy");

        REPLACEMENTS.put("phiêu lưu", "Adventure");

        REPLACEMENTS.put("tội phạm", "Crime");

        REPLACEMENTS.put("bí ẩn", "Mystery");

        REPLACEMENTS.put("hoạt hình", "Animation");

        REPLACEMENTS.put("kinh dị", "Horror");

        REPLACEMENTS.put("tiểu sử", "Biography");

        REPLACEMENTS.put("gia đình", "Family");

        REPLACEMENTS.put("chiến tranh", "War");

        REPLACEMENTS.put("lịch sử", "History");

        REPLACEMENTS.put("thể thao", "Sport");

        REPLACEMENTS.put("âm nhạc", "Music");

        REPLACEMENTS.put("tài liệu", "Documentary");

        REPLACEMENTS.put("võ thuật", "Martial Arts");

        // ==================================================
        // MOVIE DOMAIN TERMS
        // ==================================================

        REPLACEMENTS.put("đạo diễn", "director");
        REPLACEMENTS.put("diễn viên", "actor");
        REPLACEMENTS.put("thể loại", "genre");

        REPLACEMENTS.put("bộ phim", "movie");
        REPLACEMENTS.put("phim điện ảnh", "movie");
        REPLACEMENTS.put("tác phẩm", "movie");
        REPLACEMENTS.put("phim", "movie");

        REPLACEMENTS.put("đánh giá", "rating");
        REPLACEMENTS.put("yêu thích", "favorite");

        // ==================================================
        // AGE RATING
        // ==================================================

        REPLACEMENTS.put("trẻ em", "PG");
        REPLACEMENTS.put("mọi lứa tuổi", "PG");

        REPLACEMENTS.put("13+", "PG-13");
        REPLACEMENTS.put("trên 13 tuổi", "PG-13");

        REPLACEMENTS.put("người lớn", "R");
    }

    public String normalize(String question) {

        if (question == null || question.isBlank()) {
            return question;
        }

        String normalized = question;

        for (Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {

            normalized = normalized.replaceAll(
                    "(?i)" + Pattern.quote(entry.getKey()),
                    entry.getValue()
            );
        }

        return normalized.trim();
    }
}