# 简单保留指定符号+数字.py
# 用法：python 这个脚本.py 你的字体.ttf
# 输出：reduced_font.ttf  （体积通常 12~18KB）

from fontTools import subset
import sys
import os

if len(sys.argv) != 2:
    print("用法：python 这个脚本.py <你的字体文件.ttf>")
    sys.exit(1)

input_font = sys.argv[1]
output_font = "reduced_font.ttf"

if not os.path.exists(input_font):
    print(f"文件不存在：{input_font}")
    sys.exit(1)

# 你要保留的所有字符（直接写文字最简单！）
# ˚和¸一定要保留，方便上下居中
keep_text = "$0¥1-2+3;4.5:6/7,8˚9¸"

options = subset.Options()
options.layout_features = "*"      # 保留间距、kerning 等，位置不变形
options.glyph_names = True
options.notdef_outline = True
options.hinting = True             # 屏幕显示清晰
options.desubroutinize = True

font = subset.load_font(input_font, options)
subsetter = subset.Subsetter(options)
subsetter.populate(text=keep_text)   # 直接按文字保留，比码点简单多了
subsetter.subset(font)

# 额外一键干掉所有上标、下标、旧式数字（防止残留）
glyph_set = font.getGlyphSet()
to_remove = [name for name in glyph_set.keys()
             if any(k in name.lower() for k in ["sup", "sub", "superior", "inferior", "oldstyle", "onum", "subs", ".sup", ".sub"])]

print(f"额外清理 {len(to_remove)} 个角标/旧式数字")
for name in to_remove:
    del glyph_set[name]

subset.save_font(font, output_font, options)

size_kb = os.path.getsize(output_font) / 1024
print(f"\n成功！生成 → {output_font}")
print(f"文件大小：{size_kb:.1f} KB")
print("已保留：¥ $ + - 1; 2 3 / 4 5 6 , 7 8 9 . 0")
print("已自动移除所有上标、下标、旧式数字等干扰项")