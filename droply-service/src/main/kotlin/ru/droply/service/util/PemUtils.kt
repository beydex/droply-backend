package ru.droply.service.util

import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.security.spec.EncodedKeySpec
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader

object PemUtils {
    @Throws(IOException::class)
    private fun parsePEMFile(pemFile: File): ByteArray {
        Security.addProvider(BouncyCastleProvider())
        if (!pemFile.isFile || !pemFile.exists()) {
            throw FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.absolutePath))
        }
        val reader = PemReader(FileReader(pemFile))
        val pemObject: PemObject = reader.readPemObject()
        val content: ByteArray = pemObject.content
        reader.close()
        return content
    }

    private fun getPublicKey(keyBytes: ByteArray, algorithm: String): PublicKey? {
        Security.addProvider(BouncyCastleProvider())
        var publicKey: PublicKey? = null
        try {
            val kf = KeyFactory.getInstance(algorithm)
            val keySpec: EncodedKeySpec = X509EncodedKeySpec(keyBytes)
            publicKey = kf.generatePublic(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            println("Could not reconstruct the public key, the given algorithm could not be found.")
        } catch (e: InvalidKeySpecException) {
            println("Could not reconstruct the public key: " + e.message)
        }
        return publicKey
    }

    private fun getPrivateKey(keyBytes: ByteArray, algorithm: String): PrivateKey? {
        Security.addProvider(BouncyCastleProvider())
        var privateKey: PrivateKey? = null
        try {
            val kf = KeyFactory.getInstance(algorithm)
            val keySpec: EncodedKeySpec = PKCS8EncodedKeySpec(keyBytes)
            privateKey = kf.generatePrivate(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            println("Could not reconstruct the private key, the given algorithm could not be found.")
        } catch (e: InvalidKeySpecException) {
            println("Could not reconstruct the private key")
            e.printStackTrace()
        }
        return privateKey
    }

    @Throws(IOException::class)
    fun readPublicKeyFromFile(filepath: String, algorithm: String): PublicKey? {
        val bytes = parsePEMFile(File(filepath))
        return getPublicKey(bytes, algorithm)
    }

    @Throws(IOException::class)
    fun readPrivateKeyFromFile(filepath: String, algorithm: String): PrivateKey? {
        val bytes = parsePEMFile(File(filepath))
        return getPrivateKey(bytes, algorithm)
    }
}
