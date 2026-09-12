from pathlib import Path
import fitz

pdf_path = Path(r'c:\Users\athar\member2_sos_maps\assets\images\photos monuments.pdf')
out_dir = Path(r'c:\Users\athar\member2_sos_maps\assets\images')
out_dir.mkdir(exist_ok=True)

doc = fitz.open(str(pdf_path))
print(f'PDF pages: {len(doc)}')
for idx, page in enumerate(doc, start=1):
    pix = page.get_pixmap(dpi=220)
    image_path = out_dir / f'monument_{idx}.png'
    pix.save(str(image_path))
    print(f'Saved: {image_path.name}')
