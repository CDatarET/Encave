import tkinter as tk

root = tk.Tk()
root.title("Encave")
root.geometry("400x300")

label = tk.Label(root, text = "Welcome Customer")
label.pack()

button = tk.Button(root, text = "Add Sandwich")
button.pack()

root.mainloop()