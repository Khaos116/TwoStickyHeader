from fontTools.ttLib import TTFont
from fontTools.pens.boundsPen import BoundsPen
import os
import sys
# 导入 GlyphCoordinates 仅用于类型提示和操作，即使它没有在顶层被导入
from fontTools.ttLib.tables._g_l_y_f import GlyphCoordinates

def center_numerals_to_capheight(font_path):
    """
    1. 打印 CapHeight 的值。
    2. 打印数字 0 的高度。
    3. 将数字 0-9 作为一个整体计算并应用Y轴偏移，使其居中于 CapHeight 和 Baseline 0 之间。
    
    :param font_path: 字体文件的完整路径。
    """
    
    # --- 构造新文件名，确保不覆盖原文件 ---
    original_filename = os.path.basename(font_path)
    base, _ = os.path.splitext(original_filename)
    
    # 构造新文件名，添加后缀并确保输出为 .ttf
    new_font_filename = f"{base}_NUMERALS_CENTERED.ttf"
    
    font_dir = os.path.dirname(font_path)
    new_font_path = os.path.join(font_dir, new_font_filename)
    # ----------------------------------------
    
    print(f"🔄 正在处理字体文件: {font_path}")
    print(f"🆕 修改后的文件将保存为: **{new_font_path}**")
    print("-" * 50)
    
    try:
        font = TTFont(font_path)
        glyph_set = font.getGlyphSet()
        char_map = font['cmap'].getBestCmap()
        os2_table = font['OS/2']
        glyf_table = font['glyf']

        # 1. 打印 CapHeight 的值
        cap_height = os2_table.sCapHeight
        print(f"✅ 步骤 1：CapHeight 的值为: **{cap_height}**")

        # 2. 打印数字 0 的高度，并获取数字字形的边界
        target_char = '0'
        target_glyph_name = char_map.get(ord(target_char))
        
        if not target_glyph_name:
            print(f"❌ 错误：无法找到目标字符 '{target_char}' 的字形。")
            return 
            
        pen = BoundsPen(glyph_set)
        glyph_set[target_glyph_name].draw(pen)
        
        if not pen.bounds:
             print(f"❌ 警告：字形 '{target_char}' 无轮廓 (可能为空白字形)。")
             glyph_y_min, glyph_y_max = 0, 0
        else:
             # pen.bounds 结构: (xMin, yMin, xMax, yMax)
             _, glyph_y_min, _, glyph_y_max = pen.bounds

        glyph_height = glyph_y_max - glyph_y_min
        print(f"✅ 步骤 2：数字 '{target_char}' 的原始高度为: **{glyph_height}** (Min: {glyph_y_min}, Max: {glyph_y_max})")
        print("-" * 50)
        
        # 3. 让数字 0-9 居中于 CapHeight 和 Baseline 0
        
        # 以数字 '0' 为基准计算偏移量
        # 目标居中点 Y_target = CapHeight / 2.0
        # 当前字形中心线 Y_current = glyph_y_min + (glyph_height / 2.0)
        
        delta_y = (cap_height / 2.0) - (glyph_y_min + (glyph_height / 2.0))
        delta_y_int = round(delta_y)
        
        print(f"--- 步骤 3：数字字形居中应用 (0-9) ---")
        print(f" 📈 Y 轴偏移量 (Delta Y): **{delta_y_int}**")

        # 遍历数字 '0' 到 '9'
        for i in range(10):
            char = str(i)
            glyph_name = char_map.get(ord(char))
            
            if glyph_name and glyph_name in glyf_table:
                glyph = glyf_table[glyph_name]
                
                if glyph.isComposite():
                    for component in glyph.components:
                        component.y += delta_y_int
                elif hasattr(glyph, 'coordinates') and glyph.coordinates is not None:
                    # 将所有坐标应用整数偏移
                    # 重新导入 GlyphCoordinates，以防环境需要它来正确设置坐标类型
                    from fontTools.ttLib.tables._g_l_y_f import GlyphCoordinates 
                    new_coords_list = [(int(x), int(y) + delta_y_int) for x, y in glyph.coordinates]
                    glyph.coordinates = GlyphCoordinates(new_coords_list)
                    
                print(f" ✅ 数字 '{char}' ({glyph_name}) 已应用偏移。")
            # else:
            #     print(f" ⚠️ 警告：无法找到数字 '{char}' 的字形。跳过。") # 减少输出冗余
                
        # 4. 保存修改后的字体 (到新的路径)
        font.save(new_font_path)
        print("-" * 50)
        print(f"🎉 任务完成！字体已保存到: **{new_font_path}**")
        
    except Exception as e:
        print(f"❌ 发生错误: {e}")

# --- 主程序入口 ---
if __name__ == "__main__":
    
    if len(sys.argv) < 2:
        print("使用方法: python script_name.py <字体文件路径>")
        print("示例: python script_name.py C:\\Fonts\\my-font.ttf")
        sys.exit(1)

    # 从命令行参数读取字体文件路径
    FONT_FILE_PATH = sys.argv[1] 
    
    # 调用函数
    center_numerals_to_capheight(FONT_FILE_PATH)