package com.buzz.redis.stats;

/**
 * @author bairen
 * @description
 **/
public interface Option {
    //interface api begin

    boolean isDetail();

    //interface api end

    static Option valueOf(String option) {
        return option == null ? new NullOption() : new StringOption(option);
    }

    class StringOption implements Option {

        private String option;

        public StringOption(String option) {
            this.option = option;
        }

        public boolean isDetail() {
            return "-d".equals(option);
        }
    }

    class NullOption implements Option {

        @Override
        public boolean isDetail() {
            return false;
        }
    }

}

