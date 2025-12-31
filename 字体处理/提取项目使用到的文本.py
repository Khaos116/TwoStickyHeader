import os

def is_emoji(char):
    """
    判断字符是否为常见的 Emoji 或特殊符号区间
    """
    code = ord(char)
    # 常见 Emoji 范围 (涵盖了绝大部分表情、象形符号、各种杂项符号)
    if 0x1F000 <= code <= 0x1F9FF: return True  # Emoticons & Symbols
    if 0x1FA00 <= code <= 0x1FAFF: return True  # Symbols and Pictographs Extended
    if 0x2600 <= code <= 0x27BF: return True    # Misc Symbols & Dingbats
    if 0xFE00 <= code <= 0xFE0F: return True    # Variation Selectors (Emoji 样式修饰符)
    return False

def extract_android_chars_no_emoji(project_path, output_file):
    all_chars = set()
    valid_extensions = ('.xml', '.kt', '.java', '.gradle', '.kts')
    ignored_dirs = {'.git', '.gradle', 'build', '.idea', 'outputs'}
    
    print(f"🚀 开始全量扫描（已排除 Emoji）: {project_path}")
    
    file_count = 0
    for root, dirs, files in os.walk(project_path):
        dirs[:] = [d for d in dirs if d not in ignored_dirs]
        
        for file in files:
            if file.endswith(valid_extensions):
                file_path = os.path.join(root, file)
                file_count += 1
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        content = f.read()
                        for char in content:
                            # 过滤条件：1.非空白 2.非 Emoji 3.非控制字符
                            if not char.isspace() and not is_emoji(char) and ord(char) > 31:
                                all_chars.add(char)
                except Exception as e:
                    print(f"⚠️ 无法读取: {file_path} -> {e}")

    # 排序
    sorted_chars = "".join(sorted(list(all_chars)))

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(sorted_chars)

    print(f"\n--- 扫描报告 ---")
    print(f"✅ 扫描文件数: {file_count}")
    print(f"✅ 提取去重字符: {len(sorted_chars)} 个")
    print(f"✅ 结果已保存至: {output_file}")

if __name__ == "__main__":
    # 配置路径
    PROJECT_ROOT = "E:\Work\TwoStickyHeader"
    OUTPUT_NAME = "cleaned_project_chars.txt"
    extract_android_chars_no_emoji(PROJECT_ROOT, OUTPUT_NAME)