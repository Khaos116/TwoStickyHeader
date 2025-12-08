from fontTools.ttLib import TTFont
import os
import sys

def calculate_and_apply_y_offset_and_metrics_sync(font_path):
    """
    处理字体文件的垂直度量指标，不覆盖原文件，并确保输出为 TTF 格式。
    
    1. 读取并打印关键度量指标。
    2. 平衡 WinAscent 和 WinDescent (改为取最小间距)。
    3. 将 Win Metrics 同步到 Typo Metrics。
    4. 将 hhea Metrics 同步到 OS/2 Typo Metrics。
    
    :param font_path: 字体文件的完整路径。
    """
    
    # 获取原始文件名基础部分 (例如: segoe-ui-bold)
    original_filename = os.path.basename(font_path)
    base, _ = os.path.splitext(original_filename)
    
    # 构造新文件名，硬编码扩展名为 .ttf
    new_font_filename = f"{base}_METRICS_SYNC_MIN_FIX.ttf" # 修改文件名以区分逻辑
    
    font_dir = os.path.dirname(font_path)
    new_font_path = os.path.join(font_dir, new_font_filename)
    
    print(f"🔄 正在处理字体文件: {font_path}")
    print(f"🆕 修改后的文件将保存为: **{new_font_path}**")
    print(f"📄 输出格式已强制设置为: **TTF (.ttf)**")
    print("-" * 50)
    
    try:
        # fontTools 会自动处理 OTF 或 TTF 文件的读取
        font = TTFont(font_path)
        os2_table = font['OS/2']
        hhea_table = font['hhea'] 
        cap_height = os2_table.sCapHeight 
        
        # --- 步骤 1：读取并打印关键度量指标 ---
        
        print(f"--- 步骤 1：原始垂直度量报告 (CapHeight: {cap_height}) ---")
        
        # Win Metrics
        print(f" 📊 usWinAscent: {os2_table.usWinAscent}")
        print(f" 📊 usWinDescent: {os2_table.usWinDescent}")
        print(f" 📊 sCapHeight: {os2_table.sCapHeight}")
        print("-" * 30)
        
        # OS/2 Typo Metrics
        print(f" 📊 sTypoAscender: {os2_table.sTypoAscender}")
        print(f" 📊 sTypoDescender: {os2_table.sTypoDescender}")
        print(f" 📊 sTypoLineGap: {os2_table.sTypoLineGap}")
        print("-" * 30)
        
        # hhea Metrics
        print(f" 📊 hhea Ascender: {hhea_table.ascender}")
        print(f" 📊 hhea Descender: {hhea_table.descender}")
        print(f" 📊 hhea LineGap: {hhea_table.lineGap}")
        print("-" * 50)


        # --- 步骤 2：精确平衡 WinAscent/WinDescent (改为取较小的间距) ---

        old_win_ascent = os2_table.usWinAscent
        old_win_descent = os2_table.usWinDescent
        # H_top 是 CapHeight 到顶部 usWinAscent 的间距
        H_top = abs(old_win_ascent - cap_height) 
        # H_bottom 是基线到底部 usWinDescent 的间距
        H_bottom = old_win_descent
        
        # 保持 Win Metrics 的对称性，基于 CapHeight 缩小到最小间距
        
        # 比较 H_top (上方间距) 和 H_bottom (下方间距)
        if H_top > H_bottom:
            # 如果上方间距更大，将 usWinAscent 缩小，使其顶部间距等于 H_bottom
            new_win_ascent = int(round(cap_height + H_bottom))
            os2_table.usWinAscent = new_win_ascent
            # usWinDescent 保持不变 (因为它已经是较小或相等的间距)
            
        elif H_bottom > H_top:
            # 如果下方间距更大，将 usWinDescent 缩小，使其底部间距等于 H_top
            new_win_descent = int(round(H_top))
            os2_table.usWinDescent = new_win_descent
            # usWinAscent 保持不变 (因为它已经是较小或相等的间距)
        
        # 否则 (H_top == H_bottom)，两者保持不变，已经是平衡状态。

        final_win_ascent = os2_table.usWinAscent
        final_win_descent = os2_table.usWinDescent
        
        print(f"--- 步骤 2：Win 度量平衡 (以最小间距为基准) ---")
        print(f" ℹ️ 最终 usWinAscent: **{final_win_ascent}**")
        print(f" ℹ️ 最终 usWinDescent: **{final_win_descent}**")
        print("-" * 50)


        # --- 步骤 3：Win Metrics 同步到 Typo Metrics ---
        
        # sTypoAscender (必须是正值) = usWinAscent
        os2_table.sTypoAscender = final_win_ascent
        
        # sTypoDescender (必须是负值) = -usWinDescent
        os2_table.sTypoDescender = -final_win_descent
        
        # sTypoLineGap 重置为 0
        os2_table.sTypoLineGap = 0
        
        print(f"--- 步骤 3：Typo Metrics 同步完成 (同步自 Win Metrics) ---")
        print(f" ✅ sTypoAscender 设置为: **{os2_table.sTypoAscender}**")
        print(f" ✅ sTypoDescender 设置为: **{os2_table.sTypoDescender}**")
        print(f" ✅ sTypoLineGap 设置为: **{os2_table.sTypoLineGap}**")
        print("-" * 50)

        # --- 步骤 4：hhea Metrics 同步到 OS/2 Typo Metrics 的值 ---
        
        # hheaAscender = sTypoAscender (都是正值)
        hhea_table.ascender = os2_table.sTypoAscender
        
        # hheaDescender = sTypoDescender (都是负值)
        hhea_table.descender = os2_table.sTypoDescender
        
        # hheaLineGap = sTypoLineGap
        hhea_table.lineGap = os2_table.sTypoLineGap
        
        print(f"--- 步骤 4：hhea Metrics 同步到 Typo Metrics 完成 ---")
        print(f" ✅ hhea - ascender 设置为: **{hhea_table.ascender}** (同步自 sTypoAscender)")
        print(f" ✅ hhea - descender 设置为: **{hhea_table.descender}** (同步自 sTypoDescender)")
        print(f" ✅ hhea - lineGap 设置为: **{hhea_table.lineGap}** (同步自 sTypoLineGap)")
        print("-" * 50)
        
        # 4. 保存修改后的字体
        font.save(new_font_path)
        print(f"🎉 任务完成！字体已保存到: **{new_font_path}**")
        
    except Exception as e:
        print(f"❌ 发生错误: {e}")

# --- 主程序入口 (动态传入路径) ---
if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("使用方法: python script_name.py <字体文件路径>")
        print("示例: python script_name.py C:\\Fonts\\my-font.ttf")
        print("或: python script_name.py C:\\Fonts\\my-font.otf")
        sys.exit(1)

    # 从命令行参数读取字体文件路径
    FONT_FILE_PATH = sys.argv[1] 
    
    # 调用函数
    calculate_and_apply_y_offset_and_metrics_sync(FONT_FILE_PATH)