import os, struct
import datetime
import click

import Cryptodome
from Cryptodome.Cipher import AES
from Cryptodome.Util.Padding import pad, unpad
from Cryptodome.Random import get_random_bytes


KEY_BYTE_SIZE = 32  # 32-bytes = 256-bit key
NONCE_BYTES_SIZE = 16
TAG_BYTES_SIZE = 16
BATCH_SIZE = 2**16


def encrypt_file(input_file_name: str,
                 output_file_name: str = None,
                 key: str = None):

    if not key:
        key = get_random_bytes(KEY_BYTE_SIZE)

    if not output_file_name:
        output_file_name = input_file_name.split('.')[0] + '.enc'

    with open(input_file_name, 'rb') as fin:
        with open(output_file_name, 'wb') as fout:
            encryptor = AES.new(key, AES.MODE_GCM)

            fout.write(key)
            fout.write(encryptor.nonce)

            while True:
                chunk = fin.read(BATCH_SIZE)
                if not chunk:
                    break
                fout.write(encryptor.encrypt(chunk))

            tag = encryptor.digest()
            fout.write(tag)


def decrypt_file(input_file_name: str,
                 output_file_name: str = None):

    if not output_file_name:
        output_file_name = input_file_name.split('.')[0] + '.dec'

    payload_size = os.path.getsize(input_file_name) - KEY_BYTE_SIZE - NONCE_BYTES_SIZE - TAG_BYTES_SIZE

    with open(input_file_name, 'rb') as fin:
        key = fin.read(KEY_BYTE_SIZE)
        nonce = fin.read(NONCE_BYTES_SIZE)
        decryptor = AES.new(key, AES.MODE_GCM, nonce=nonce)

        with open(output_file_name, 'wb') as fout:

            for _ in range(int(payload_size / BATCH_SIZE)):
                chunk = fin.read(BATCH_SIZE)
                fout.write(decryptor.decrypt(chunk))

            chunk = fin.read(int(payload_size % BATCH_SIZE))
            fout.write(decryptor.decrypt(chunk))

            tag = fin.read(TAG_BYTES_SIZE)

            try:
                decryptor.verify(tag)
            except ValueError as e:
                print("Decryption finished with error!")
                os.remove(output_file_name)
                raise e


@click.command()
@click.option('--encrypt', is_flag=True, required=False)
@click.option('--decrypt', is_flag=True, required=False)
@click.argument('input_file', nargs=1, type=click.Path(exists=True), required=False)
@click.argument('output_file', nargs=1, type=click.Path(exists=True), required=False)
def main(encrypt: bool, decrypt: bool, input_file: str, output_file: str):

    if not input_file:
        input_file = input('Enter input file name or path:')

    if not encrypt and not decrypt:
        crypt = input('Do you want Encrypt or Decrypt  e/d?')
        if crypt == 'e':
            encrypt = True
        elif crypt == 'd':
            decrypt = True

    start = datetime.datetime.now()
    if encrypt:
        encrypt_file(input_file_name=input_file, output_file_name=None)

    if decrypt:
        decrypt_file(input_file_name=input_file, output_file_name=None)

    end = datetime.datetime.now()

    print("File has size: ", os.path.getsize(input_file))
    print("Crypting time: ", end-start)

    rem = input("Do you want to remove input file y/n?")
    if rem == 'y':
        os.remove(input_file)

    input('Press any key to exit.')


if __name__ == '__main__':
    main()
