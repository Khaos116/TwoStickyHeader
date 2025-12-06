# -*- coding: utf-8 -*-
# ds-digib 终极银行级版本：所有数字+字母等宽560，统一右留59，符号不动
from fontTools.ttLib import TTFont

font = TTFont('ds-digib.ttf')
NEW_AW = 560
RIGHT_MARGIN = 59          # 所有数字字母右边统一留白59

# 所有需要改的字形（数字+字母，ds-digib.ttf 真实名字全列）
TARGET_GLYPHS = {
    'zero', 'one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine',
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    'uni0030','uni0031','uni0032','uni0033','uni0034','uni0035','uni0036','uni0037','uni0038','uni0039',
    'uni0041','uni0042','uni0043','uni0044','uni0045','uni0046','uni0047','uni0048','uni0049',
    'uni0061','uni0062','uni0063','uni0064','uni0065','uni0066','uni0067','uni0068','uni0069',
}

modified = 0
for glyph_name in font.getGlyphOrder():
    if glyph_name not in TARGET_GLYPHS:
        continue

    glyph = font['glyf'][glyph_name]

    # 1. 强制宽度560
    font['hmtx'][glyph_name] = (NEW_AW, 0)  # 先临时设lsb=0

    # 2. 计算真实视觉宽度
    if not hasattr(glyph, 'xMin') or not hasattr(glyph, 'xMax'):
        continue
    visual_width = glyph.xMax - glyph.xMin
    if visual_width <= 0:
        continue

    # 3. 统一右留59 → lsb = 560 - 59 - 字形真实宽度
    new_lsb = NEW_AW - RIGHT_MARGIN - visual_width

    font['hmtx'][glyph_name] = (NEW_AW, new_lsb)
    modified += 1

# 更新最大宽度
font['hhea'].advanceWidthMax = max(NEW_AW, font['hhea'].advanceWidthMax)

# 保存
font.save('ds-digib-数字字母560-统一右留59.ttf')
print("银行级终极版生成完毕！")
print(f"已处理 {modified} 个字形（所有数字+字母）")
print("全部等宽560，右边统一留白59，符号原样不动")
print("生成文件：ds-digib-数字字母560-统一右留59.ttf")