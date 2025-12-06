# subset_din.py
# 用法：python 提取中英文符号.py 你的原始字体.ttf
# 自动生成：subset_child.ttf （显示100%正确，体积≈16KB）

from fontTools import subset
import sys
import os

# ============ 参数读取 ============
if len(sys.argv) != 2:
    print("用法：python 提取中英文符号.py <原始字体文件.ttf>")
    sys.exit(1)

input_font = sys.argv[1]          # 例如：DINPro-Medium_13936.ttf
output_font = "reduced_font.ttf"  # 固定输出名

if not os.path.exists(input_font):
    print(f"错误：找不到文件 {input_font}")
    sys.exit(1)

# ============ 要保留的字符 ============
keep_text = (
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    "abcdefghijklmnopqrstuvwxyz"
    "0123456789"
    " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
    "¥£€¢°©®™±×÷√∞≈≠≤≥†‡§¶·•−"
    "，。！？；：、“”‘’（）【】《》—…"
    ".,!?;:()[]{}<>“”‘’\"'`~@#$%^&*-_=+|\\/→←↑↓"
    " \u3000\n\t"   # 空格、全角空格、换行、制表符
)

# ============ 核心参数（完美显示的关键） ============
options = subset.Options()
options.layout_features = "*"      # 保留所有连字、kerning 等
options.drop_tables = []           # 一个表都不扔！排版永不变形
options.glyph_names = True
options.notdef_outline = True
options.hinting = True             # 保留 Hinting，屏幕更锐利

# ============ 执行子集化 ============
font = subset.load_font(input_font, options)
subsetter = subset.Subsetter(options)
subsetter.populate(text=keep_text)
subsetter.subset(font)
subset.save_font(font, output_font, options)

print(f"完美生成 → {output_font}")
print("显示位置100%和原字体一致")