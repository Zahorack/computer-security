import os
import datetime
import click

from Cryptodome.Cipher import AES
from Cryptodome.Random import get_random_bytes
from Cryptodome.PublicKey import RSA
from Cryptodome.Cipher import PKCS1_OAEP

KEY_BYTE_SIZE = 32
KEY_BIT_SIZE = KEY_BYTE_SIZE * 8
NONCE_BYTES_SIZE = 16
TAG_BYTES_SIZE = 16
BATCH_SIZE = 2 ** 16

RSA_KEY_INIT_SIZE = 2048
RSA_KEY_BYTE_SIZE = KEY_BIT_SIZE


def encrypt_file(input_file_name: str,
                 public_rsakey_file: str,
                 output_file_name: str = None,
                 key: str = None):
    with open(public_rsakey_file, 'rb') as fpub:
        public_key = RSA.import_key(fpub.read())

    if not key:
        key = get_random_bytes(KEY_BYTE_SIZE)

    if not output_file_name:
        output_file_name = input_file_name.split('.')[0] + '.enc'

    encryptor = AES.new(key, AES.MODE_GCM)

    with open(input_file_name, 'rb') as fin:
        with open(output_file_name, 'wb') as fout:
            fout.write(PKCS1_OAEP.new(public_key).encrypt(key))
            fout.write(encryptor.nonce)

            while True:
                chunk = fin.read(BATCH_SIZE)
                if not chunk:
                    break
                fout.write(encryptor.encrypt(chunk))

            tag = encryptor.digest()
            fout.write(tag)


def decrypt_file(input_file_name: str,
                 private_rsakey_file: str,
                 output_file_name: str = None):
    if not output_file_name:
        output_file_name = input_file_name.split('.')[0] + '.dec'

    with open(private_rsakey_file, 'rb') as fpriv:
        private_key = RSA.import_key(fpriv.read())

    payload_size = os.path.getsize(input_file_name) - KEY_BIT_SIZE - NONCE_BYTES_SIZE - TAG_BYTES_SIZE

    with open(input_file_name, 'rb') as fin:
        key = PKCS1_OAEP.new(private_key).decrypt(fin.read(RSA_KEY_BYTE_SIZE))
        nonce = fin.read(NONCE_BYTES_SIZE)
        decrypter = AES.new(key, AES.MODE_GCM, nonce=nonce)

        with open(output_file_name, 'wb') as fout:

            for _ in range(int(payload_size / BATCH_SIZE)):
                chunk = fin.read(BATCH_SIZE)
                fout.write(decrypter.decrypt(chunk))

            chunk = fin.read(int(payload_size % BATCH_SIZE))
            fout.write(decrypter.decrypt(chunk))

            tag = fin.read(TAG_BYTES_SIZE)
            try:
                decrypter.verify(tag)
            except ValueError as e:
                print(f"Decryption finished with error, integrity was corrupted! {e}")


def generate_rsa_keypair(file_name: str = None, passphrase: str = None):
    key = RSA.generate(RSA_KEY_INIT_SIZE)

    if not file_name:
        file_name = 'rsa_key'

    with open(file_name + '_private.pem', 'wb') as fpriv:
        fpriv.write(key.export_key(passphrase=passphrase))

    with open(file_name + '_public.pem', 'wb') as fpub:
        fpub.write(key.publickey().export_key(passphrase=passphrase))


@click.command()
@click.option('--encrypt', is_flag=True, required=False)
@click.option('--decrypt', is_flag=True, required=False)
@click.option('--generate', is_flag=True, required=False)
@click.argument('rsa_key_file', type=click.Path(exists=True), required=False)
@click.argument('input_file', type=click.Path(exists=True), required=False)
@click.argument('output_file', type=click.Path(exists=True), required=False)
def main(encrypt: bool, decrypt: bool, generate: bool, input_file: str, output_file: str, rsa_key_file: str):
    if generate:
        generate_rsa_keypair(rsa_key_file)
        return

    if not rsa_key_file:
        rsa_key_file = input('Enter rsa key file name or path:')

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
        encrypt_file(input_file_name=input_file, public_rsakey_file=rsa_key_file, output_file_name=output_file)
    if decrypt:
        decrypt_file(input_file_name=input_file, private_rsakey_file=rsa_key_file, output_file_name=output_file)

    end = datetime.datetime.now()

    print("File has size: ", os.path.getsize(input_file))
    print("Crypting time: ", end - start)

    rem = input("Do you want to remove input file y/n?")
    if rem == 'y':
        os.remove(input_file)

    input('Press any key to exit.')


if __name__ == '__main__':
    main()
