package fabric;

public enum ColorUtil {
    
	RED(255, 0, 0),
    BLUE(0, 0, 255),
    GREEN(0, 255, 0),
    LIGHTGREEN(144, 238, 144),
    BLACK(0, 0, 0),
    WHITE(255, 255, 255),
    YELLOW(255, 255, 0),
	ORANGE(255, 165, 0),
	PINK(255, 0, 127),
	AQUA(0, 255, 255)
	;
    /**
     * RGBの各成分から16進数の色コードを生成します。
     * @param red   赤の成分 (0-255)
     * @param green 緑の成分 (0-255)
     * @param blue  青の成分 (0-255)
     * @return 16進数の色コード
     */
	private final int rgb;
	
	ColorUtil(int r, int g, int b) {
		// 各成分が0から255の範囲であることを確認
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("Color components must be between 0 and 255.");
        }

        this.rgb = (r << 16) | (g << 8) | b;
    }
	
    public int getRGB() {
    	return this.rgb;
    }
}
