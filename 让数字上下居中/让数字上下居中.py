from fontTools.ttLib import TTFont

def sync_vertical_metrics(from_font_path, to_font_path, out_path):
    # 读取字体
    src = TTFont(from_font_path)
    dst = TTFont(to_font_path)

    # ------------- 1. OS/2 Table -------------
    for key in ["sTypoAscender", "sTypoDescender", "sTypoLineGap",
                "usWinAscent", "usWinDescent"]:
        if hasattr(src["OS/2"], key):
            value = getattr(src["OS/2"], key)
            setattr(dst["OS/2"], key, value)
            print(f"Copied OS/2.{key}: {value}")

    # ------------- 2. hhea Table -------------
    for key in ["ascent", "descent", "lineGap"]:
        if hasattr(src["hhea"], key):
            value = getattr(src["hhea"], key)
            setattr(dst["hhea"], key, value)
            print(f"Copied hhea.{key}: {value}")

    # 保存输出
    dst.save(out_path)
    print(f"\nDone! Saved new font → {out_path}")


if __name__ == "__main__":
    sync_vertical_metrics(
        from_font_path="数字上下居中.ttf",
        to_font_path="数字上下不居中.ttf",
        out_path="新的字体上下居中.ttf"
    )
