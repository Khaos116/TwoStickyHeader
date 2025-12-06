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

# 要保留的字符（unicode 字符即可）
keep_text = "$0¥1-2+3;4.5:6/7,8˚9¸"

# -------------------------
# subset 配置（关键）
# -------------------------
options = subset.Options()
options.layout_features = "*"
options.glyph_names = True
options.notdef_outline = True
options.hinting = True
options.desubroutinize = True

# ❗关键：不自动扩展相关 glyph，只保留我们明确要的
options.closure = False

# ❗关键：忽略未映射字符，不要报错
options.ignore_missing_unicodes = True

# 载入字体
font = subset.load_font(input_font, options)

# 只保留我们想要的文字
subsetter = subset.Subsetter(options)
subsetter.populate(text=keep_text)
subsetter.subset(font)

# 自动保存
subset.save_font(font, output_font, options)

size_kb = os.path.getsize(output_font) / 1024
print(f"\n成功！生成 → {output_font}")
print(f"文件大小：{size_kb:.1f} KB")
print("已保留字符：", keep_text)
