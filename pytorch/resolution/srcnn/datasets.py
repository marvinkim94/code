from PIL import Image
import torchvision.transforms as transforms
from torch.utils.data import Dataset


class TrainDataset(Dataset):
    def __init__(self, paths):
        self.paths = paths
        self.transform1 = transforms.ToTensor()
        self.transform2 = transforms.GaussianBlur(kernel_size=5, sigma=(1.0, 1.5))

    def __getitem__(self, idx):
        path = self.paths[idx]

        lab = Image.open(path)
        lab = self.transform1(lab)
        img = self.transform2(lab)

        return img, lab

    def __len__(self):
        return len(self.paths)