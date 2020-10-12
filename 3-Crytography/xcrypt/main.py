import os, struct
import datetime
import click

from Cryptodome.Cipher import AES
from Cryptodome.Util.Padding import pad, unpad
from Cryptodome.Random import get_random_bytes


def encrypt_file(input_file_name: str,
                 output_file_name: str = None,
                 key: str = None):

    if not key:
        key = get_random_bytes(32)

    with open('key.bin', 'wb') as key_file:
        key_file.write(key)

    if not output_file_name:
        output_file_name = input_file_name.split('.')[0] + '.enc'

    with open(input_file_name, 'rb') as fin:
        with open(output_file_name, 'wb') as fout:
            encryptor = AES.new(key, AES.MODE_CBC)
            fout.write(struct.pack('<Q', os.path.getsize(input_file_name)))
            fout.write(encryptor.iv)

            while True:
                chunk = fin.read(2**16)
                if not chunk:
                    break
                elif len(chunk) % AES.block_size != 0:
                    chunk = pad(chunk, AES.block_size)

                fout.write(encryptor.encrypt(chunk))


def decrypt_file(input_file_name: str,
                 key_file_name: str,
                 output_file_name: str = None):

    if not output_file_name:
        output_file_name = input_file_name.split('.')[0] + '.dec'

    with open(key_file_name, 'rb') as key_file:
        key = key_file.read()

    with open(input_file_name, 'rb') as fin:
        origsize = struct.unpack('<Q', fin.read(struct.calcsize('Q')))[0]
        iv = fin.read(16)
        decryptor = AES.new(key, AES.MODE_CBC, iv=iv)

        with open(output_file_name, 'wb') as fout:
            while True:
                chunk = fin.read(2**16)
                if not chunk:
                    break
                fout.write(decryptor.decrypt(chunk))

            fout.truncate(origsize)


@click.command()
@click.option('--encode', is_flag=True, required=False)
@click.option('--decode', is_flag=True, required=False)
@click.argument('input_file', nargs=1, type=click.Path(exists=True), required=False)
@click.argument('key_file', nargs=1, type=click.Path(exists=True), required=False)
def main(encode: bool, decode: bool, input_file: str, key_file: str):

    if not input_file:
        input_file = input('Enter input file name or path:')

    if not encode and not decode:
        crypt = input('Do you want Encrypt or Decrypt  e/d?')
        if crypt == 'e':
            encode = True
        elif crypt == 'd':
            decode = True
            if not key_file:
                key_file = input('Enter please key file: ')

    start = datetime.datetime.now()
    if encode:
        encrypt_file(input_file_name=input_file, output_file_name=None)

    if decode:
        decrypt_file(input_file_name=input_file, key_file_name=key_file)

    end = datetime.datetime.now()

    print("File has size: ", os.path.getsize(input_file))
    print("Crypting time: ", end-start)

    rem = input("Do you want to remove input file y/n?")
    if rem == 'y':
        os.remove(input_file)

    input('Press any key to exit.')


if __name__ == '__main__':
    main()
