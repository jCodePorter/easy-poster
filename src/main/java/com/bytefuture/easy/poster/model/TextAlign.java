package com.bytefuture.easy.poster.model;

/**
 * Horizontal alignment inside a text block.
 */
public enum TextAlign {
    LEFT {
        @Override
        public int offset(int layoutWidth, int lineWidth) {
            return 0;
        }
    },

    CENTER {
        @Override
        public int offset(int layoutWidth, int lineWidth) {
            return (layoutWidth - lineWidth) / 2;
        }
    },

    RIGHT {
        @Override
        public int offset(int layoutWidth, int lineWidth) {
            return layoutWidth - lineWidth;
        }
    },

    JUSTIFY {
        @Override
        public int offset(int layoutWidth, int lineWidth) {
            return 0;
        }
    };

    public abstract int offset(int layoutWidth, int lineWidth);
}
