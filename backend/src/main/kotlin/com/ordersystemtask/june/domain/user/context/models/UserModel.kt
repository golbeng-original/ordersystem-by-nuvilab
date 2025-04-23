package com.ordersystemtask.june.domain.user.context.models

import com.ordersystemtask.june.domain.user.entity.OAuthProviderType
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "User")
@Table(name = "user")
class UserModel(

    @Id
    @Column(name = "user_id", columnDefinition = "INT UNSIGNED")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Long = 0,

    @Column(name = "email", unique = true, nullable = false)
    val email:String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "trait", nullable = false)
    var trait:UserTraitType = UserTraitType.Normal,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted:Boolean = false,

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null,

    @OneToMany(fetch = FetchType.EAGER, targetEntity = ReceiveAddressModel::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id", unique = true)
    var receiveAddresses: List<ReceiveAddressModel> = listOf(),

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    var oauthProvider: OAuthProviderModel? = null,
)

@Entity(name = "ReceiveAddress")
@Table(name = "receive_address")
class ReceiveAddressModel(
    @Id
    @Column(name = "receive_address_id", columnDefinition = "VARCHAR(50)")
    val receiveAddressId: String,

    @Column(name = "owner_name", nullable = false)
    var ownerName: String,

    @Column(name = "address", nullable = false)
    var address: String,

    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,
)

@Entity(name = "OAuthProvider")
@Table(name = "oauth_provider")
class OAuthProviderModel(
    @Id
    @Column(name = "user_id")
    val userId:Long? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    val user: UserModel,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type")
    val providerType: OAuthProviderType = OAuthProviderType.None,

    @Column(name = "sub")
    val sub:String,

    @Column(name = "email")
    val email:String,

    @Column(name = "refresh_token")
    var refreshToken:String

)