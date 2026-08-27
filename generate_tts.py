import asyncio
import edge_tts

TEXT = "欢迎使用TraeIDE进行智能开发"
VOICE = "zh-CN-XiaoyiNeural"  # 温和年轻女声，支持情感表达
OUTPUT = r"d:\trae_out\welcome_trae.mp3"

async def main():
    # rate: 中等语速(默认); pitch: 略微上扬体现愉快
    communicate = edge_tts.Communicate(
        text=TEXT,
        voice=VOICE,
        rate="+0%",
        pitch="+2Hz",
        proxy=None,
    )
    # 添加愉快情感风格
    communicate = edge_tts.Communicate(
        text=TEXT,
        voice=VOICE,
        rate="+0%",
        pitch="+2Hz",
    )
    await communicate.save(OUTPUT)
    print(f"OK: {OUTPUT}")

if __name__ == "__main__":
    asyncio.run(main())
