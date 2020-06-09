package com.benchmark.domain;

import java.io.Serializable;
import java.util.Objects;

public class Trade implements Serializable {

    private String id;
    private String externalId;
    private String info;

    private String str1;
    private String str2;
    private String str3;
    private String str4;
    private String str5;
    private String str6;
    private String str7;
    private String str8;
    private String str9;
    private String str10;

    private Long long1;
    private Long long2;
    private Long long3;
    private Long long4;
    private Long long5;
    private Long long6;
    private Long long7;
    private Long long8;
    private Long long9;
    private Long long10;

    public Trade() {}

    public Trade(String id, String externalId, String info, String str1, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, Long long1, Long long2, Long long3, Long long4, Long long5, Long long6, Long long7, Long long8, Long long9, Long long10) {
        this.id = id;
        this.externalId = externalId;
        this.info = info;
        this.str1 = str1;
        this.str2 = str2;
        this.str3 = str3;
        this.str4 = str4;
        this.str5 = str5;
        this.str6 = str6;
        this.str7 = str7;
        this.str8 = str8;
        this.str9 = str9;
        this.str10 = str10;
        this.long1 = long1;
        this.long2 = long2;
        this.long3 = long3;
        this.long4 = long4;
        this.long5 = long5;
        this.long6 = long6;
        this.long7 = long7;
        this.long8 = long8;
        this.long9 = long9;
        this.long10 = long10;
    }

    public String getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getInfo() {
        return info;
    }

    public String getStr1() {
        return str1;
    }

    public String getStr2() {
        return str2;
    }

    public String getStr3() {
        return str3;
    }

    public String getStr4() {
        return str4;
    }

    public String getStr5() {
        return str5;
    }

    public String getStr6() {
        return str6;
    }

    public String getStr7() {
        return str7;
    }

    public String getStr8() {
        return str8;
    }

    public String getStr9() {
        return str9;
    }

    public String getStr10() {
        return str10;
    }

    public Long getLong1() {
        return long1;
    }

    public Long getLong2() {
        return long2;
    }

    public Long getLong3() {
        return long3;
    }

    public Long getLong4() {
        return long4;
    }

    public Long getLong5() {
        return long5;
    }

    public Long getLong6() {
        return long6;
    }

    public Long getLong7() {
        return long7;
    }

    public Long getLong8() {
        return long8;
    }

    public Long getLong9() {
        return long9;
    }

    public Long getLong10() {
        return long10;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }

    public void setStr4(String str4) {
        this.str4 = str4;
    }

    public void setStr5(String str5) {
        this.str5 = str5;
    }

    public void setStr6(String str6) {
        this.str6 = str6;
    }

    public void setStr7(String str7) {
        this.str7 = str7;
    }

    public void setStr8(String str8) {
        this.str8 = str8;
    }

    public void setStr9(String str9) {
        this.str9 = str9;
    }

    public void setStr10(String str10) {
        this.str10 = str10;
    }

    public void setLong1(Long long1) {
        this.long1 = long1;
    }

    public void setLong2(Long long2) {
        this.long2 = long2;
    }

    public void setLong3(Long long3) {
        this.long3 = long3;
    }

    public void setLong4(Long long4) {
        this.long4 = long4;
    }

    public void setLong5(Long long5) {
        this.long5 = long5;
    }

    public void setLong6(Long long6) {
        this.long6 = long6;
    }

    public void setLong7(Long long7) {
        this.long7 = long7;
    }

    public void setLong8(Long long8) {
        this.long8 = long8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return Objects.equals(id, trade.id) &&
                Objects.equals(externalId, trade.externalId) &&
                Objects.equals(info, trade.info) &&
                Objects.equals(str1, trade.str1) &&
                Objects.equals(str2, trade.str2) &&
                Objects.equals(str3, trade.str3) &&
                Objects.equals(str4, trade.str4) &&
                Objects.equals(str5, trade.str5) &&
                Objects.equals(str6, trade.str6) &&
                Objects.equals(str7, trade.str7) &&
                Objects.equals(str8, trade.str8) &&
                Objects.equals(str9, trade.str9) &&
                Objects.equals(str10, trade.str10) &&
                Objects.equals(long1, trade.long1) &&
                Objects.equals(long2, trade.long2) &&
                Objects.equals(long3, trade.long3) &&
                Objects.equals(long4, trade.long4) &&
                Objects.equals(long5, trade.long5) &&
                Objects.equals(long6, trade.long6) &&
                Objects.equals(long7, trade.long7) &&
                Objects.equals(long8, trade.long8) &&
                Objects.equals(long9, trade.long9) &&
                Objects.equals(long10, trade.long10);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, info, str1, str2, str3, str4, str5, str6, str7, str8, str9, str10, long1, long2, long3, long4, long5, long6, long7, long8, long9, long10);
    }

    public void setLong9(Long long9) {
        this.long9 = long9;
    }

    public void setLong10(Long long10) {
        this.long10 = long10;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "id='" + id + '\'' +
                ", externalId='" + externalId + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
